package com.example.carsharing.service;

import com.example.carsharing.dto.CarDto;
import com.example.carsharing.dto.CreateCarRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {

    CarDto save(CreateCarRequestDto requestDto);

    Page<CarDto> getAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto update(Long id,CreateCarRequestDto requestDto);

    void deleteById(Long id);

}

