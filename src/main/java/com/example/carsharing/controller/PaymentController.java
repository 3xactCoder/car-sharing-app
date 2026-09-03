package com.example.carsharing.controller;

import com.example.carsharing.dto.payment.CreatePaymentSessionRequestDto;
import com.example.carsharing.dto.payment.PaymentResponseDto;
import com.example.carsharing.model.User;
import com.example.carsharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payment Management",
        description = "Endpoints for managing payments and Stripe sessions")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    @Operation(summary = "Get payment history",
            description = "Managers see all/filtered payments, customers see only their own")
    public List<PaymentResponseDto> getPayments(
            @RequestParam(name = "user_id", required = false) Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        return paymentService.getPayments(userId, currentUser);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/")
    @Operation(summary = "Create payment session",
            description = "Creates a Stripe session for the given rental and type")
    public PaymentResponseDto createSession(
            @RequestBody @Valid CreatePaymentSessionRequestDto requestDto,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return paymentService.createSession(requestDto, uriComponentsBuilder);
    }

    @GetMapping("/success/")
    @Operation(summary = "Payment success callback",
            description = "Callback endpoint where Stripe redirects on successful payment")
    public PaymentResponseDto paymentSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.verifySuccessfulPayment(sessionId);
    }

    @GetMapping("/cancel/")
    @Operation(summary = "Payment cancel callback",
            description = "Returns payment paused message")
    public String paymentCancel() {
        return "Payment was canceled or paused. The session remains active for 24 hours.";
    }
}
