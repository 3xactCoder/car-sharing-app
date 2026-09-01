package com.example.carsharing.dto.rental;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull
    @FutureOrPresent
    private LocalDate returnDate;

    @NotNull
    private Long carId;
}
