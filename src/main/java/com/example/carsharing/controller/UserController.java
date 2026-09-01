package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.dto.user.UserUpdateProfileDto;
import com.example.carsharing.dto.user.UserUpdateRoleDto;
import com.example.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRoleDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    public UserResponseDto getMyProfile(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @PatchMapping("/me")
    public UserResponseDto updateMyProfile(
            Authentication authentication,
            @RequestBody @Valid UserUpdateProfileDto requestDto
    ) {
        return userService.updateProfile(authentication.getName(), requestDto);
    }
}
