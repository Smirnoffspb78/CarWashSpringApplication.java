package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.exception.ServiceNotFoundException;
import com.smirnov.carwashspring.repository.CarWashServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный слой услуг.
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CarWashServiceService {

    /**
     * Репозиторий услуг.
     */
    private final CarWashServiceRepository carWashServiceRepository;

    private final ModelMapper modelMapper;

    /**
     * Возвращает весь список услуг.
     *
     * @return Список услуг
     */

    @Transactional(readOnly = true)
    public List<CarWashServiceDTO> getAllServices() {
        log.info("getAllServices");
        /*return carWashServiceRepository.findAll().stream()
                .map(service -> modelMapper.map(service, CarWashServiceDTO.class))
                .toList();*/
        return carWashServiceRepository.findAll().stream()
                .map(carWashService ->
                        new CarWashServiceDTO(carWashService.getName(), carWashService.getPrice(), carWashService.getTime()))
                .toList();
    }

    /**
     * Сохраняет новую услугу в БД
     *
     * @param serviceDTO Услуга
     */
    public Integer save(CarWashServiceDTO serviceDTO) {
        CarWashService carWashService = new CarWashService();
        carWashService.setName(serviceDTO.name());
        carWashService.setPrice(serviceDTO.price());
        carWashService.setTime(serviceDTO.time());
        return carWashServiceRepository.save(carWashService).getId();
    }

    /**
     * Возвращает услугу по ее идентификатору.
     *
     * @param id Идентификатор услуги
     * @return Услуга
     */
    public CarWashService getServiceById(Integer id) {
        return carWashServiceRepository.findById(id).orElseThrow(() -> new ServiceNotFoundException("Сервис с id не существует"));
    }
}
