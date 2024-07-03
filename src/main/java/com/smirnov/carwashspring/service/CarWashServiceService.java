package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.request.create.CarWashServiceCreateDTO;
import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
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
        log.info("Просмотрены все предоставляемые услуги");
        return carWashServiceRepository.findAll().stream()
                .map(service -> modelMapper.map(service, CarWashServiceDTO.class))
                .toList();
    }

    /**
     * Сохраняет новую услугу в БД
     *
     * @param serviceDTO Услуга
     */
    public Integer save(CarWashServiceCreateDTO serviceDTO) {

            CarWashService carWashService = modelMapper.map(serviceDTO, CarWashService.class);
            Integer carWashServiceId = carWashServiceRepository.save(carWashService).getId();
            log.info("Добавлена новая услуга с id: {}", carWashServiceId);
            return carWashServiceId;
    }

    /**
     * Возвращает услугу по ее идентификатору.
     *
     * @param id Идентификатор услуги
     * @return Услуга
     */
    public CarWashService getServiceById(Integer id) {
        return carWashServiceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(CarWashService.class, id));
    }
}
