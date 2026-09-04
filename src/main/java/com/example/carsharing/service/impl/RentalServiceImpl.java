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
import com.example.carsharing.service.NotificationService;
import com.example.carsharing.service.RentalService;
import java.time.LocalDate;
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
    private final NotificationService notificationService;

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

    @Override
    @Transactional
    public RentalResponseDto returnRental(Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id: " + id)
        );
        if (user.getRole() != User.Role.MANAGER && !rental.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this rental");
        }
        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("Rental has already been returned");
        }

        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);

        Rental updatedRental = rentalRepository.save(rental);

        notificationService.sendMessage(String.format(
                "🏁 Rental Returned!\nRental ID: %d\nUser: %s\nCar: %s %s\nActual Return Date: %s",
                updatedRental.getId(),
                rental.getUser().getEmail(),
                car.getBrand(),
                car.getModel(),
                updatedRental.getActualReturnDate()
        ));

        return rentalMapper.toDto(updatedRental);
    }

    @Override
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(today);

        if (overdueRentals.isEmpty()) {
            notificationService.sendMessage("No rentals overdue today!");
            return;
        }

        for (Rental rental : overdueRentals) {
            String message = String.format(
                    "⚠️ OVERDUE RENTAL ALERT ⚠️%n"
                            + "Rental ID: %d%n"
                            + "User: %s %s (Email: %s)%n"
                            + "Car: %s %s%n"
                            + "Rental Date: %s%n"
                            + "Expected Return Date: %s",
                    rental.getId(),
                    rental.getUser().getFirstName(),
                    rental.getUser().getLastName(),
                    rental.getUser().getEmail(),
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getRentalDate(),
                    rental.getReturnDate()
            );
            notificationService.sendMessage(message);
        }
    }
}
