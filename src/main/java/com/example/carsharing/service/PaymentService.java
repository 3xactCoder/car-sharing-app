package com.example.carsharing.service;

import com.example.carsharing.dto.payment.CreatePaymentSessionRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {
    PaymentResponseDto createSession(
            CreatePaymentSessionRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder
    );

    Page<PaymentResponseDto> getPayments(Long userId,
                                         Pageable pageable,
                                         Authentication authentication);

    PaymentResponseDto verifySuccessfulPayment(String sessionId);
}
