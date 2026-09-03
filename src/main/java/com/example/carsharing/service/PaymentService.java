package com.example.carsharing.service;

import com.example.carsharing.dto.payment.CreatePaymentSessionRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.model.User;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {
    PaymentResponseDto createSession(
            CreatePaymentSessionRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder
    );

    List<PaymentResponseDto> getPayments(Long userId, User currentUser);

    PaymentResponseDto verifySuccessfulPayment(String sessionId);
}
