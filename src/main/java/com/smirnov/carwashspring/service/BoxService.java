package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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

    /**
     * Сервисный слой записей.
     */
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
        Integer id = boxRepository.save(box).getId();
        log.info("{}. Box with id {} save in db_car_wash ", HttpStatus.CREATED, id);
        return id;
    }

    /**
     * Обновляет информацию по боксу
     */
    public void updateBox(Integer id, BoxCreateDTO boxCreateDTO) {
        if (!boxRepository.existsById(id)) {
            throw new EntityNotFoundException(Box.class, id);
        }
        Box box = modelMapper.map(boxCreateDTO, Box.class);
        box.setId(id);
        boxRepository.save(box);
        log.info("{}, Box with id {} update in db_car_wash ", HttpStatus.NO_CONTENT, id);
    }

    /**
     * Возвращает список записей по идентификатору бокса
     *
     * @param id Идентификатор бокса.
     * @return Список записей.
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingById(Integer id, Integer operatorId) {
        Box box = getBoxById(id);
        checkAccessOperator(box, operatorId);
        List<RecordingDTO> recordingDTOS = recordingService.getRecordingDTOS(box.getRecordings());
        log.info("{}. Получен список всех записей по идентификатору бокса", HttpStatus.OK);
        return recordingDTOS;
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

    /**
     * Возвращает бокс по его идентификатору
     *
     * @param id Идентификатор бокса
     * @return Бокс
     */
    public Box getBoxById(Integer id) {
        return boxRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Box.class, id));
    }

    /**
     * Проверяет права доступа оператора к боксу
     *
     * @param box Бокс
     */
    public void checkAccessOperator(Box box, Integer operatorId) {
        if (box.getUser().getRole().getRolesUser().equals(RolesUser.ROLE_OPERATOR) && (!operatorId.equals(box.getUser().getId()))) {
            throw new ForbiddenAccessException(operatorId);
        }
    }
}
