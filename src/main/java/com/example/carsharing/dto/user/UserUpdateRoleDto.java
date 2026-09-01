package com.example.carsharing.dto.user;

import com.example.carsharing.model.User.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRoleDto {
    @NotNull
    private Role role;
}
