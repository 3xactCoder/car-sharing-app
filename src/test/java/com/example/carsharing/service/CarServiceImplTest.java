package com.example.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharing.dto.car.CarDto;
import com.example.carsharing.dto.car.CreateCarRequestDto;
import com.example.carsharing.exceptions.EntityNotFoundException;
import com.example.carsharing.mapper.CarMapper;
import com.example.carsharing.model.Car;
import com.example.carsharing.repository.CarRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.carsharing.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarDto carDto;
    private CreateCarRequestDto requestDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setModel("Camry");
        car.setBrand("Toyota");
        car.setType(Car.CarType.SEDAN);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(45.0));

        carDto = new CarDto();
        carDto.setId(1L);
        carDto.setModel("Camry");
        carDto.setBrand("Toyota");

        requestDto = new CreateCarRequestDto();
        requestDto.setModel("Camry");
        requestDto.setBrand("Toyota");
    }

    @Test
    @DisplayName("Save car - valid request")
    void save_ValidRequestDto_ReturnsCarDto() {
        when(carMapper.toEntity(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.save(requestDto);

        assertNotNull(actual);
        assertEquals(carDto.getModel(), actual.getModel());
        verify(carRepository, times(1)).save(car);
    }

    @Test
    @DisplayName("Find car by id - existing car")
    void findById_ExistingId_ReturnsCarDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.getById(1L);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());
    }

    @Test
    @DisplayName("Find car by id - non existing car throws exception")
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(carRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.getById(100L));
    }

    @Test
    @DisplayName("Get all cars with pagination")
    void findAll_ValidPageable_ReturnsPageOfCarDtos() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(pageable)).thenReturn(carPage);

        when(carMapper.toDto(car)).thenReturn(carDto);

        Page<CarDto> actual = carService.getAll(pageable);

        assertNotNull(actual);
        assertEquals(1, actual.getTotalElements());
        assertEquals(carDto.getModel(), actual.getContent().get(0).getModel());
    }

    @Test
    @DisplayName("Delete car by id")
    void deleteById_ValidId_CallsRepositoryDelete() {
        doNothing().when(carRepository).deleteById(1L);
        carService.deleteById(1L);
        verify(carRepository, times(1)).deleteById(1L);
    }
}