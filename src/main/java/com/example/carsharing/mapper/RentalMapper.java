package com.example.carsharing.mapper;

import com.example.carsharing.config.MapperConfig;
import com.example.carsharing.dto.rental.CreateRentalRequestDto;
import com.example.carsharing.dto.rental.RentalResponseDto;
import com.example.carsharing.model.Car;
import com.example.carsharing.model.Rental;
import com.example.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, imports = {java.time.LocalDate.class})
public interface RentalMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "rentalDate", expression = "java(LocalDate.now())")
    @Mapping(source = "car", target = "car")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "requestDto.returnDate", target = "returnDate")
    Rental toEntity(CreateRentalRequestDto requestDto, Car car, User user);
}
