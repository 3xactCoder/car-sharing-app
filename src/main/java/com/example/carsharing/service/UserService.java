package com.example.carsharing.service;

import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.dto.user.UserUpdateProfileDto;
import com.example.carsharing.dto.user.UserUpdateRoleDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto getProfile(String email);

    UserResponseDto updateProfile(String email, UserUpdateProfileDto requestDto);

    UserResponseDto updateRole(Long id, UserUpdateRoleDto requestDto);
}
