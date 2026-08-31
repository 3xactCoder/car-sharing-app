package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.CarDto;
import com.example.carsharing.dto.CreateCarRequestDto;
import com.example.carsharing.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toEntity(CreateCarRequestDto requestDto);

    void updateEntityFromDto(CreateCarRequestDto requestDto, @MappingTarget Car car);
}
