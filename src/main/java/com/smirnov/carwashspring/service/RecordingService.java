package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingCompletedDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.exception.RecordingCreateException;
import com.smirnov.carwashspring.repository.RecordingRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;

/**
 * Запись.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecordingService {
    private enum Action {SAVE, UPDATE}

    @Getter
    @AllArgsConstructor
    private static class RecordingInBox {
        private Box box;
        private LocalDateTime start;
        private LocalDateTime finish;
        private int timeInBox;
    }

    /**
     * Репозиторий записи.
     */
    private final RecordingRepository recordingRepository;
    /**
     * Сервисный слой бокса.
     */
    private final BoxService boxService;
    /**
     * Сервисный слой пользователя.
     */
    private final UserService userService;
    /**
     * Сервисный слой услуг.
     */
    private final CarWashServiceService carWashServiceService;

    private final ModelMapper modelMapper;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return Выручка за заданный промежуток времени
     */
    @Transactional(readOnly = true)
    public BigDecimal getProfit(RangeDataTimeDTO rangeDataTimeDTO) {
        BigDecimal profit = recordingRepository.findSumByRange(rangeDataTimeDTO.getStart(), rangeDataTimeDTO.getFinish())
                .orElse(BigDecimal.valueOf(0.0));
        log.info("{}. Получена выручка за диапазон {} - {}", HttpStatus.OK, rangeDataTimeDTO.getStart(), rangeDataTimeDTO.getFinish());
        return profit;
    }

    /**
     * Возвращает список всех записей за диапазон даты, время.
     *
     * @param rangeDTDto Диапазон даты, времени
     * @return Список записей
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingsByRange(RangeDataTimeDTO rangeDTDto) {
        List<RecordingDTO> recordingDTOS =
                getRecordingDTOS(recordingRepository.findByStartBetween(rangeDTDto.getStart(), rangeDTDto.getFinish()));
        log.info("{}. Получен список всех записей за диапазон {} - {}", HttpStatus.OK, rangeDTDto.getStart(), rangeDTDto.getFinish());
        return recordingDTOS;
    }

    /**
     * Отменяет запись по ее идентификатору (удаляет, клиент больше не может ее подтвердить).
     *
     * @param id Идентификатор записи
     */
    public void cancellationReserveById(Integer id) {
        Recording recording = getRecordingByIdNotRemovedAndNotCompleted(id);
        checkAccessRecording(recording);
        recording.setReserved(false);
        recording.setArrived(false);
        recording.setRemoved(true);
        log.info("{}. Запись с id {} удалена", HttpStatus.NO_CONTENT, id);
    }

    /**
     * Подтверждает запись по ее идентификатору.
     *
     * @param id Идентификатор записи
     */
    public void approve(Integer id) {
        Recording recording = recordingRepository.findByIdAndRemovedIsFalseAndReservedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(RecordingService.class, id));
        if (!recording.getUser().getId().equals(jwtAuthenticationFilter.getAuthUser().getId())) {
            throw new ForbiddenAccessException(id);
        }
        recording.setReserved(true);
        log.info("{}. Запись с id {} подтверждена", HttpStatus.NO_CONTENT, id);
    }

    /**
     * Отмечает клиента, как прибывшего.
     * @param id Идентификатор записи
     */
    public void arrive(Integer id){
        Recording recording = recordingRepository.findByIdAndReservedIsTrueAndArrivedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(RecordingService.class, id));
        if (!recording.getUser().getId().equals(jwtAuthenticationFilter.getAuthUser().getId())) {
            throw new ForbiddenAccessException(id);
        }
        recording.setArrived(true);
        log.info("{}. Клиент с id {} прибыл", HttpStatus.NO_CONTENT, jwtAuthenticationFilter.getAuthUser().getId());
    }

    /**
     * Отмечает запись, как завершенную, если она не завершена, по ее идентификатору.
     *
     * @param id Идентификатор записи
     */
    public void updateCompleteById(Integer id) {
        Recording recordingUpdate = recordingRepository.findByIdAndArrivedIsTrueAndCompletedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id отсутствует или она уже завершенная"));
        boxService.checkAccessOperator(recordingUpdate.getBox());
        recordingUpdate.setCompleted(true);
        log.info("{}. Запись с id {} выполнена", HttpStatus.NO_CONTENT, id);
    }

    /**
     * Создает запись
     *
     * @param recordingDTO Параметры записи
     * @return Идентификатор созданной записи
     */
    public Integer createRecordingByIdUser(RecordingCreateDTO recordingDTO) {
        User user = userService.getUserById(recordingDTO.idUser());
        if (!user.getId().equals(jwtAuthenticationFilter.getAuthUser().getId())) {
            throw new ForbiddenAccessException(user.getId());
        }
        Set<CarWashService> services = recordingDTO.idServices()
                .stream()
                .map(carWashServiceService::getServiceById)
                .collect(Collectors.toSet());
        //Получаем бокс для записи
        RecordingInBox accessibleBox = getBoxRecord(services, recordingDTO, null, Action.SAVE);
        //Самое быстрое окончание выполнения записи
        LocalDateTime finishRecord = accessibleBox.getFinish();
        //Проверяем, есть ли записи у юзера в это время.
        if (!recordingRepository.findByUserIdAndStartAndFinishAndRemovedIsFalse(recordingDTO.idUser(), recordingDTO.start(), finishRecord).isEmpty()) {
            throw new RecordingCreateException("У пользователя с id %d уже есть записи на это время".formatted(user.getId()));
        }
        //Создаем запись
        Recording recording = new Recording();
        recording.setUser(user);
        recording.setStart(recordingDTO.start());
        recording.setCreated(LocalDateTime.now());
        recording.setFinish(finishRecord);
        recording.setCost(calculateCost(user, services));
        recording.setBox(accessibleBox.getBox());
        recording.setServices(services);
        Integer recordingId = recordingRepository.save(recording).getId();
        log.info("{}. Запись с id {} создана", HttpStatus.CREATED, recordingId);
        return recordingId;
    }

    /**
     * Обновляет запись по ее идентификатору
     *
     * @param id           Идентификатор записи
     * @param recordingDTO Новые параметры записи
     */
    public void updateRecording(Integer id, RecordingCreateDTO recordingDTO) {
        Recording recording = getRecordingByIdNotRemovedAndNotCompleted(id);
        checkAccessRecording(recording);
        User user = recording.getUser();
        Set<CarWashService> services = recordingDTO.idServices()
                .stream()
                .map(carWashServiceService::getServiceById)
                .collect(Collectors.toSet());
        RecordingInBox accessibleBox = getBoxRecord(services, recordingDTO, id, Action.UPDATE);
        //Проверяем, есть ли записи у юзера в это кроме той, что правим.
        LocalDateTime finishRecord = accessibleBox.getFinish();
        if (!recordingRepository.findByIdAndUserIdAndStartAndFinishAndRemovedIsFalse(user.getId(), recordingDTO.start(), finishRecord, id).isEmpty()) {
            throw new RecordingCreateException("У пользователя с id %d уже есть записи на это время".formatted(user.getId()));
        }
        //Меняем
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(calculateCost(user, services));
        recording.setBox(accessibleBox.getBox());
        recording.setServices(services);
        log.info("{}. Запись с id {} изменена", HttpStatus.NO_CONTENT, id);
    }

    /**
     * Возвращает активные брони пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return Список активных броней
     */
    @Transactional(readOnly = true)
    public List<RecordingReservedDTO> getAllActiveReserveByUserId(Integer userId) {
        checkAccessRecording(userId);
        List<RecordingReservedDTO> recordingReservedDTOS = recordingRepository.findAllByUser_IdAndReservedIsTrueAndCompletedIsFalse(userId)
                .stream()
                .map(recording -> {
                    RecordingReservedDTO rsd = modelMapper.map(recording, RecordingReservedDTO.class);
                    rsd.setServicesName(recording.getServices().stream()
                            .map(CarWashService::getName)
                            .collect(Collectors.toSet()));
                    return rsd;
                })
                .toList();
        log.info("{}. Получен список всех активных броней пользователя с id {}", HttpStatus.OK, userId);
        return recordingReservedDTOS;
    }

    /**
     * Возвращает список выполненных записей по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return Список выполненных записей
     */
    @Transactional(readOnly = true)
    public List<RecordingCompletedDTO> getAllCompletedRecordingByUserId(Integer userId) {
        checkAccessRecording(userId);
        List<RecordingCompletedDTO> recordingCompletedDTOS = recordingRepository.findAllByUser_IdAndCompletedIsTrue(userId).stream()
                .map(r -> modelMapper.map(r, RecordingCompletedDTO.class))
                .toList();
        log.info("{}. Получен список выполненных записей пользователя с id {}", HttpStatus.OK, userId);
        return recordingCompletedDTOS;
    }

    /**
     * Возвращает список всех записей за диапазон даты времени по идентификатору бокса.
     *
     * @param rangeDTDTO Диапазоны даты,
     * @param boxId      Идентификатор бокса
     * @return Список записей.
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingsByRangeAndIdBox(RangeDataTimeDTO rangeDTDTO, Integer boxId) {
        Box box = boxService.getBoxById(boxId);
        boxService.checkAccessOperator(box);
        List<RecordingDTO> recordingDTOS
                = getRecordingDTOS(recordingRepository.findByBoxAndStartBetween(box, rangeDTDTO.getStart(), rangeDTDTO.getFinish()));
        log.info("Получен список всех записей бокса с id {}", boxId
        );
        return recordingDTOS;
    }

    /**
     * Преобразует список записей Entity в список записей DTO
     *
     * @param recordings Список записей Entity
     * @return Список записей DTO
     */
    public List<RecordingDTO> getRecordingDTOS(List<Recording> recordings) {
        return recordings.stream().map(
                recording -> {
                    RecordingDTO recordingDTO = modelMapper.map(recording, RecordingDTO.class);
                    recordingDTO.setIdServices(recording.getServices()
                            .stream()
                            .map(CarWashService::getId)
                            .collect(Collectors
                                    .toSet()));
                    return recordingDTO;
                }).toList();
    }

    /**
     * Возвращает неудаленную и невыполненную запись по ее идентификатору.
     *
     * @param id Идентификатор записи
     * @return Запись
     */
    public Recording getRecordingByIdNotRemovedAndNotCompleted(Integer id) {
        return recordingRepository.findByIdAndRemovedIsFalseAndCompletedIsFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(RecordingService.class, id));
    }

    /**
     * Возвращается список всех неотмененных записей пользователя
     *
     * @param user Пользователь
     * @return Список записей
     */
    public List<Recording> getAllRecordingsUser(User user) {
        return recordingRepository.findAllByUserAndRemovedIsFalse(user);
    }

    private RecordingInBox getBoxRecord(Set<CarWashService> services, RecordingCreateDTO recordingDTO, Integer id, Action action) {
        //Получаем базовое время выполнения всех услуг в записи
        int baseTimeAllWork = services.stream()
                .mapToInt(CarWashService::getTime)
                .sum();
        //Формируем список всех доступных для записи боксов.
        List<RecordingInBox> accessibleBoxes = boxService.getAllBoxes().stream()
                .map(box -> {
                    int timeInBoxLocal = (int) ceil(box.getUsageRate() * baseTimeAllWork);
                    return new RecordingInBox(box, recordingDTO.start(), recordingDTO.start().plusMinutes(timeInBoxLocal), timeInBoxLocal);
                })
                .filter(r -> checkWorkTime(r.getStart(), r.getFinish(), r.getBox(), id, action))
                .sorted(Comparator.comparingDouble(r -> r.getBox().getUsageRate()))
                .toList();
        if (accessibleBoxes.isEmpty()) {
            throw new RecordingCreateException("Свободное время отсутствует");
        }
        return accessibleBoxes.get(0);
    }

    private BigDecimal calculateCost(User user, Set<CarWashService> services) {
        //Получаем сумму всех услуг с учетом скидки
        BigDecimal discount = new BigDecimal(user.getDiscount());
        BigDecimal costWithDiscount = new BigDecimal(0);
        for (CarWashService service : services) {
            costWithDiscount = costWithDiscount.add(service.getPrice());
        }
        return costWithDiscount.subtract(costWithDiscount.multiply(discount));
    }

    private boolean checkWorkTime(LocalDateTime start, LocalDateTime finish, Box box, Integer id, Action action) {
        //Проверяем не выпадаем ли на следующий день (круглосуточных боксов нет)
        if (finish.toLocalDate().isAfter(start.toLocalDate())) {
            return false;
        }
        //Проверяем работает ли бокс в эти часы
        if (start.toLocalTime().isBefore(box.getStart()) || finish.toLocalTime().isAfter(box.getFinish())) {
            return false;
        }
        switch (action) {
            case UPDATE -> {
                //Проверяем, есть ли другие записи в боксе в это время, не включая эту запись, т.к. она все равно будет перенесена
                if (!recordingRepository.findByIdAndBoxIdAndRemovedIsFalse(box.getId(), start, finish, id).isEmpty()) {
                    return false;
                }
            }
            case SAVE -> {
                //Проверяем, есть ли другие записи в боксе в это время
                if (!recordingRepository.findByBoxIdAndRemovedIsFalse(box.getId(), start, finish).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Вспомогательный метод проверяет возможность доступа к записи
     *
     * @param userId Идентификатор пользователя
     */
    private void checkAccessRecording(Integer userId) {
        UserDetailsCustom userDetailsCustom = jwtAuthenticationFilter.getAuthUser();
        Integer idUserCustom = userDetailsCustom.getId();
        RolesUser roleUserCustom = userDetailsCustom.getRolesUser();
        if ((RolesUser.ROLE_OPERATOR.equals(roleUserCustom) || RolesUser.ROLE_USER.equals(roleUserCustom))
                && !userId.equals(idUserCustom)) {
            throw new ForbiddenAccessException(userId);
        } else {
            userService.checkUserById(userId);
        }
    }

    /**
     * Вспомогательный метод проверяет возможность доступа к записи
     *
     * @param recording запись
     */
    private void checkAccessRecording(Recording recording) {
        UserDetailsCustom userDetailsCustom = jwtAuthenticationFilter.getAuthUser();
        switch (userDetailsCustom.getRolesUser()) {
            case ROLE_USER -> {
                if (!userDetailsCustom.getId().equals(recording.getUser().getId())) {
                    throw new ForbiddenAccessException(userDetailsCustom.getId());
                }
            }
            case ROLE_OPERATOR -> {
                if (!userDetailsCustom.getId().equals(recording.getUser().getId())
                        || !userDetailsCustom.getId().equals(recording.getBox().getUser().getId())) {
                    throw new ForbiddenAccessException(userDetailsCustom.getId());
                }
            }
        }
        if (!LocalDateTime.now().isBefore(recording.getStart())) {
            throw new ForbiddenAccessException(userDetailsCustom.getId());
        }
    }
}
