package com.example.carsharing.service.impl;

import com.example.carsharing.dto.CarDto;
import com.example.carsharing.dto.CreateCarRequestDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.repository.CarRepository;
import com.example.carsharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        Car car = carMapper.toEntity(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public Page<CarDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto);
    }

    @Override
    public CarDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id" + id)
        );
        return carMapper.toDto(car);
    }

    @Override
    @Transactional
    public CarDto update(Long id, CreateCarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id" + id)
        );
        carMapper.updateEntityFromDto(requestDto,car);
        return carMapper.toDto(carRepository.save(car));

    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);

    }
}
