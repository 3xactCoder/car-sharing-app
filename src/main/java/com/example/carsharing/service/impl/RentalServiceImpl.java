package com.example.carsharing.service.impl;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.mapper.RentalMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import com.example.carsharing.repository.CarRepository;
import com.example.carsharing.repository.RentalRepository;
import com.example.carsharing.service.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    @Transactional
    public RentalResponseDto save(
            CreateRentalRequestDto requestDto,
            Authentication authentication
    ) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + requestDto.getCarId())
        );
        if (car.getInventory() <= 0) {
            throw new IllegalStateException("Car is not available for rental");
        }
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        User user = (User) authentication.getPrincipal();

        Rental rental = rentalMapper.toEntity(requestDto, car, user);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalResponseDto> getRentalsByFilter(
            Long userId,
            Boolean isActive,
            Pageable pageable,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        boolean isManager = user.getRole() == User.Role.MANAGER;

        Long filterUserId = isManager ? userId : user.getId();
        return rentalRepository.findByUserIdAndIsActive(filterUserId, isActive, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalResponseDto findById(Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: " + id)
        );
        if (user.getRole() != User.Role.MANAGER && !rental.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this rental");
        }
        return rentalMapper.toDto(rental);
    }
}
