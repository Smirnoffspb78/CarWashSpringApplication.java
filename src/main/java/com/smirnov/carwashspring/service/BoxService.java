package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.exception.BoxNotFountException;
import com.smirnov.carwashspring.repository.BoxRepository;
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
        return boxRepository.save(box).getId();
    }


    /**
     * Возвращает список записей по идентификатору бокса
     * @param id Идентификатор бокса.
     * @return Список записей.
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingById(Integer id) {
        return recordingService.getRecordingDTOS(boxRepository.findById(id)
                .orElseThrow(() -> new BoxNotFountException("box not found Exception"))
                .getRecordings());

    }

    /**
     * Проверяет наличие бокса по его идентификатору
     * @param id Идентификатор бокса.
     */
    public void checkBoxById(Integer id) {
        if (!boxRepository.existsById(id)) {
            throw new BoxNotFountException("Бокс с id " + id + " не найден");
        }
    }

    /**
     * Возвращает список всех боксов.
     * @return Список боксов.
     */
    public List<Box> getAllBoxes() {
        return boxRepository.findAll();
    }
}
