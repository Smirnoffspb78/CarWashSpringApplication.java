package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.BoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервисный слой бокса.
 */
@Service
@Transactional
@Slf4j
public class BoxService {

    /**
     * Репозиторий бокса.
     */
    private final BoxRepository boxRepository;

    private final RecordingService recordingService;

    private final ModelMapper modelMapper;

    public BoxService(BoxRepository boxRepository, @Lazy RecordingService recordingService, ModelMapper modelMapper) {
        this.boxRepository = boxRepository;
        this.recordingService = recordingService;
        this.modelMapper = modelMapper;
    }

    /**
     * Регистрирует новый бокс.
     *
     * @param boxCreateDto Создаваемый бокс
     */
    public Integer save(BoxCreateDTO boxCreateDto) {
        Box box = modelMapper.map(boxCreateDto, Box.class);
        Integer boxId = boxRepository.save(box).getId();
        log.info("Box save in db_car_wash with id: %d".formatted(boxId));
        return boxId;
    }


    /**
     * Возвращает список записей по идентификатору бокса
     *
     * @param id Идентификатор бокса.
     * @return Список записей.
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingById(Integer id) {
        List<RecordingDTO> recordingDTOS = recordingService.getRecordingDTOS(boxRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Box with id %d not found".formatted(id));
                    return new EntityNotFoundException(Box.class, id);
                })
                .getRecordings());
        log.info("Получен список всех записей по идентификатору бокса");
        return recordingDTOS;
    }

    /**
     * Проверяет наличие бокса по его идентификатору
     *
     * @param id Идентификатор бокса.
     */
    public void checkBoxById(Integer id) {
        if (!boxRepository.existsById(id)) {
            throw new EntityNotFoundException(Box.class, id);
        }
    }

    /**
     * Возвращает список всех боксов.
     *
     * @return Список боксов.
     */
    public List<Box> getAllBoxes() {
        List<Box> boxes = boxRepository.findAll();
        log.info("Получен список всех боксов");
        return boxes;
    }
}
