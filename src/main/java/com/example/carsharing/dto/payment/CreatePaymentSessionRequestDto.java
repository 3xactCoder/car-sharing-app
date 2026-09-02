package com.example.carsharing.dto.payment;

import com.example.carsharing.model.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentSessionRequestDto {
    @NotNull
    private Long rentalId;

    @NotNull
    private Payment.Type paymentType;
}
