package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.request.create.CarWashServiceCreateDTO;
import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.repository.CarWashServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class CarWashServiceServiceTest {

    @Mock
    CarWashServiceRepository carWashServiceRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CarWashServiceService carWashServiceService;

    @Test
    void getAllServices() {
        List<CarWashService> carWashServices = List.of(getCarWashService01(), getCarWashService02());
        CarWashServiceDTO carWashServiceDTO01 = getCarWashServiceDTO01();
        CarWashServiceDTO carWashServiceDTO02 = getCarWashServiceDTO02();
        List<CarWashServiceDTO> carWashServiceDTOS = List.of(carWashServiceDTO01, carWashServiceDTO02);

        doReturn(carWashServices).when(carWashServiceRepository).findAll();
        doReturn(carWashServiceDTO01).when(modelMapper).map(carWashServices.get(0), CarWashServiceDTO.class);
        doReturn(carWashServiceDTO02).when(modelMapper).map(carWashServices.get(1), CarWashServiceDTO.class);

        assertEquals(carWashServiceDTOS, carWashServiceService.getAllServices());
    }

    @Test
    void save() {
        CarWashService carWashService = getCarWashService01();
        CarWashServiceCreateDTO carWashServiceDTO = new CarWashServiceCreateDTO(carWashService.getName(), carWashService.getPrice(), carWashService.getTime());
        doReturn(carWashService).when(modelMapper).map(carWashServiceDTO, CarWashService.class);
        doReturn(carWashService).when(carWashServiceRepository).save(carWashService);
        assertEquals(carWashServiceService.save(carWashServiceDTO), carWashService.getId());
    }

    @Test
    void getServiceById() {
        Optional<CarWashService> optionalCarWashService = Optional.of(getCarWashService01());
        CarWashService carWashService = optionalCarWashService.orElseThrow();
        doReturn(optionalCarWashService).when(carWashServiceRepository).findById(carWashService.getId());
        assertEquals(carWashService, carWashServiceService.getServiceById(carWashService.getId()));
    }

    private CarWashService getCarWashService01(){
        CarWashService carWashService = new CarWashService();
        carWashService.setId(1);
        carWashService.setName("name");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    private CarWashService getCarWashService02(){
        CarWashService carWashService = new CarWashService();
        carWashService.setId(2);
        carWashService.setName("name02");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    private CarWashServiceDTO getCarWashServiceDTO01(){
        CarWashServiceDTO carWashService = new CarWashServiceDTO();
        carWashService.setName("name");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    private CarWashServiceDTO getCarWashServiceDTO02(){
        CarWashServiceDTO carWashService = new CarWashServiceDTO();
        carWashService.setName("name02");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }
}