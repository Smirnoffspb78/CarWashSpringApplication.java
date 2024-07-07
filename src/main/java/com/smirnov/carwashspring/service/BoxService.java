package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import com.smirnov.carwashspring.service.security.UserDetailsCustom;
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

    private final RecordingService recordingService;

    private final ModelMapper modelMapper;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public BoxService(BoxRepository boxRepository, @Lazy RecordingService recordingService, ModelMapper modelMapper, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.boxRepository = boxRepository;
        this.recordingService = recordingService;
        this.modelMapper = modelMapper;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Регистрирует новый бокс.
     *
     * @param boxCreateDto Создаваемый бокс
     */
    public Integer save(BoxCreateDTO boxCreateDto) {
        Box box = modelMapper.map(boxCreateDto, Box.class);
        Integer boxId = boxRepository.save(box).getId();
        log.info("{}. Box save in db_car_wash with id: {}", HttpStatus.CREATED, boxId);
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
        Box box = getBoxById(id);
        checkAccessOperator(box);
        List<RecordingDTO> recordingDTOS = recordingService.getRecordingDTOS(box.getRecordings());
        log.info("{}. Получен список всех записей по идентификатору бокса", HttpStatus.OK);
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

    public Box getBoxById(Integer id) {
        return boxRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Box.class, id));
    }

    public void  checkAccessOperator(Box box){
        UserDetailsCustom userDetails = jwtAuthenticationFilter.getAuthUser();
        if (userDetails.getRolesUser().equals(RolesUser.ROLE_OPERATOR) && (!userDetails.getId().equals(box.getUser().getId()))) {
            throw new ForbiddenAccessException(userDetails.getId());
        }
    }
}
