package com.example.carsharing.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateProfileDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
