package com.example.carsharing.service.impl;

import com.example.carsharing.dto.payment.CreatePaymentSessionRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.mapper.PaymentMapper;
import com.example.carsharing.model.Payment;
import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.PaymentRepository;
import com.example.carsharing.repository.RentalRepository;
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final BigDecimal CENTS_IN_DOLLAR = BigDecimal.valueOf(100);

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    @Transactional
    public PaymentResponseDto createSession(
            CreatePaymentSessionRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Rental rental = rentalRepository.findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental not found with id: " + requestDto.getRentalId()));

        BigDecimal amountToPay = calculateAmount(rental, requestDto.getPaymentType());

        String successUrl = uriComponentsBuilder.replacePath("/payments/success/")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();

        String cancelUrl = uriComponentsBuilder.replacePath("/payments/cancel/")
                .build().toUriString();

        long stripeAmount = amountToPay.multiply(CENTS_IN_DOLLAR).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(stripeAmount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(requestDto.getPaymentType()
                                                                        == Payment.Type.FINE
                                                                        ? "Rental fine"
                                                                        : "Rental payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe checkout session", e);
        }

        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(requestDto.getPaymentType());
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmountToPay(amountToPay);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponseDto> getPayments(Long userId, User currentUser) {
        if (currentUser.getRole() == User.Role.CUSTOMER) {
            return paymentRepository.findAllByUserId(currentUser.getId()).stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }
        if (userId != null) {
            return paymentRepository.findAllByUserId(userId).stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto verifySuccessfulPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session: " + sessionId));

        try {
            Session session = Session.retrieve(sessionId);
            if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
                payment.setStatus(Payment.Status.PAID);
                Payment savedPayment = paymentRepository.save(payment);
                notificationService.sendMessage("Successful payment! Rental ID: "
                        + payment.getRental().getId() + ", Amount: $" + payment.getAmountToPay());
                return paymentMapper.toDto(savedPayment);
            }
        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve Stripe session", e);
        }
        return paymentMapper.toDto(payment);
    }

    private BigDecimal calculateAmount(Rental rental, Payment.Type type) {
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        if (type == Payment.Type.FINE) {
            long overdueDays = ChronoUnit.DAYS.between(
                    rental.getReturnDate(),
                    rental.getActualReturnDate() != null
                            ? rental.getActualReturnDate()
                            : rental.getReturnDate()
            );
            return BigDecimal.valueOf(Math.max(0, overdueDays))
                    .multiply(dailyFee)
                    .multiply(FINE_MULTIPLIER);
        }
        long rentalDays = ChronoUnit.DAYS.between(
                rental.getRentalDate(),
                rental.getReturnDate()
        );
        return BigDecimal.valueOf(Math.max(1, rentalDays)).multiply(dailyFee);
    }
}
