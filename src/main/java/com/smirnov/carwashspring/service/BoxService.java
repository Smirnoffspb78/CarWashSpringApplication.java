package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.BoxNotFountException;
import com.smirnov.carwashspring.repository.BoxRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный слой бокса.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BoxService {

    /**
     * Репозиторий бокса.
     */
    private final BoxRepository boxRepository;
    /**
     * Репозиторий пользователя.
     */
    private final UserService userService;

    private final ModelMapper modelMapper;
    /**
     * Регистрирует новый бокс.
     *
     * @param boxCreateDto Создаваемый бокс
     */
    public Integer save(BoxCreateDTO boxCreateDto) {
        Role role = new Role();
        role.setRolesUser(RolesUser.OPERATOR);
        //Box box = modelMapper.map(boxCreateDto, Box.class);
        User user = userService.getUserByIdAndRole(boxCreateDto.userId(), role);
        Box box = new Box();
        box.setFinish(boxCreateDto.finish());
        box.setStart(boxCreateDto.start());
        box.setUsageRate(boxCreateDto.usageRate());
        user.setId(boxCreateDto.userId());
        box.setUser(user);
        return boxRepository.save(box).getId();
    }


    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingById(Integer id) {
        return boxRepository.findById(id).orElseThrow(()->new BoxNotFountException("")).getRecordings().stream().map(
                recording -> RecordingDTO.builder()
                        .id(recording.getId())
                        .idUser(recording.getUser().getId())
                        .start(recording.getStart())
                        .finish(recording.getFinish())
                        .reserved(recording.isReserved())
                        .complited(recording.isCompleted())
                        .cost(recording.getCost())
                        .idBox(recording.getBox().getId())
                        .idServices(recording.getServices()
                                .stream()
                                .map(CarWashService::getId)
                                .collect(Collectors
                                        .toSet()))
                        .build()).toList();
    }


    public void checkBoxById(Integer id) {
        if (!boxRepository.existsById(id)) {
            throw new BoxNotFountException("Бокс с id "+id+" не найден");
        }
    }

    public List<Box>  getAllBoxes(){
        return boxRepository.findAll();
    }
}
