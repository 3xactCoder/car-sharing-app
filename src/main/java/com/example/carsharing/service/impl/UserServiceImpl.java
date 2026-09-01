package com.example.carsharing.service.impl;

import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.dto.user.UserUpdateProfileDto;
import com.example.carsharing.dto.user.UserUpdateRoleDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.exceptions.RegistrationException;
import com.example.carsharing.mapper.UserMapper;
import com.example.carsharing.model.User;
import com.example.carsharing.model.User.Role;
import com.example.carsharing.repository.UserRepository;
import com.example.carsharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new RegistrationException("Passwords do not match");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User already exists with email: "
                    + requestDto.getEmail());
        }
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(Role.CUSTOMER);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found by email: " + email)
        );
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(String email, UserUpdateProfileDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found by email: " + email)
        );
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateRole(Long id, UserUpdateRoleDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found by id: " + id)
        );
        user.setRole(requestDto.getRole());
        return userMapper.toDto(userRepository.save(user));
    }
}
