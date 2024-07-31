package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.request.create.CarWashServiceCreateDTO;
import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
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

import static com.smirnov.carwashspring.AppTest.ILLEGAL_ID;
import static com.smirnov.carwashspring.AppTest.getCarWashService01;
import static com.smirnov.carwashspring.AppTest.getCarWashService02;
import static com.smirnov.carwashspring.AppTest.getCarWashServiceDTO01;
import static com.smirnov.carwashspring.AppTest.getCarWashServiceDTO02;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void getAllServices_ListCarWashServiceDTO() {
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
        CarWashService carWashService = getCarWashService01();

        doReturn(Optional.of(carWashService)).when(carWashServiceRepository).findById(carWashService.getId());

        assertEquals(carWashService, carWashServiceService.getServiceById(carWashService.getId()));
    }

    @Test
    void getServiceById_EntityNotFoundException() {
        doReturn(Optional.empty()).when(carWashServiceRepository).findById(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, ()->carWashServiceService.getServiceById(ILLEGAL_ID));
    }
}