package com.example.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.user.UserRegistrationRequestDto;
import com.example.carsharing.dto.user.UserResponseDto;
import com.example.carsharing.dto.user.UserUpdateRoleDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.exceptions.RegistrationException;
import com.example.carsharing.mapper.UserMapper;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.UserRepository;
import java.util.Optional;

import com.example.carsharing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setFirstName("John");
        userResponseDto.setLastName("Doe");
    }

    @Test
    @DisplayName("Register new user successfully")
    void register_NewUser_ReturnsUserResponseDto() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.register(request);

        assertNotNull(actual);
        assertEquals("test@example.com", actual.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Register user throws exception when email exists")
    void register_ExistingEmail_ThrowsRegistrationException() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(RegistrationException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Update user role successfully")
    void updateRole_ValidId_ReturnsUpdatedUser() {
        UserUpdateRoleDto roleDto = new UserUpdateRoleDto();
        roleDto.setRole(User.Role.MANAGER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        userService.updateRole(1L, roleDto);

        assertEquals(User.Role.MANAGER, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Update role throws exception when user not found")
    void updateRole_NonExistingId_ThrowsEntityNotFoundException() {
        UserUpdateRoleDto roleDto = new UserUpdateRoleDto();
        roleDto.setRole(User.Role.MANAGER);

        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateRole(100L, roleDto));
    }
}