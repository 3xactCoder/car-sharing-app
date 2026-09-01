package com.example.carsharing.service;

import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto save(CreateRentalRequestDto requestDto, Authentication authentication);

    List<RentalResponseDto> getRentalsByFilter(
            Long userId,
            Boolean isActive,
            Pageable pageable,
            Authentication authentication
    );

    RentalResponseDto findById(Long id, Authentication authentication);
}
