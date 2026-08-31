package com.example.carsharing.dto;

import com.example.carsharing.model.Car.CarType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;

    @NotBlank
    private String brand;

    @NotNull
    private CarType type;

    @Positive
    private int inventory;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal dailyFee;
}
