package com.example.carsharing.controller;

import com.example.carsharing.dto.user.UserLoginRequestDto;
import com.example.carsharing.dto.user.UserLoginResponseDto;
import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.security.AuthenticationService;
import com.example.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
