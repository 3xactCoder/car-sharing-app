package com.example.carsharing.controller;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.service.RentalService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto addRental(
            @RequestBody @Valid CreateRentalRequestDto requestDto,
            Authentication authentication
    ) {
        return rentalService.save(requestDto, authentication);
    }

    @GetMapping
    public List<RentalResponseDto> getRentals(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active", required = false) Boolean isActive,
            Pageable pageable,
            Authentication authentication
    ) {
        return rentalService.getRentalsByFilter(userId, isActive, pageable, authentication);
    }

    @GetMapping("/{id}")
    public RentalResponseDto getRentalById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return rentalService.findById(id, authentication);
    }

    @PostMapping("/{id}/return")
    public RentalResponseDto returnRental(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return rentalService.returnRental(id, authentication);
    }
}
