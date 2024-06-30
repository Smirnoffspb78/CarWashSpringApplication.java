package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingComplitedDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.RecordingNotFoundException;
import com.smirnov.carwashspring.repository.RecordingRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
 * Запись
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
     * Репозиторий бокса.
     */
    private final BoxService boxService;
    /**
     * Репозиторий пользователя.
     */
    private final UserService userService;

    /**
     * Репозиторий услуг.
     */
    private final CarWashServiceService carWashServiceService;

    private final ModelMapper modelMapper;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return Выручка за заданный промежуток времени
     */
    @Transactional(readOnly = true)
    public BigDecimal getProfit(RangeDataTimeDTO rangeDataTimeDTO) {
        return recordingRepository.findSumByRange(rangeDataTimeDTO.start(), rangeDataTimeDTO.finish())
                .orElse(BigDecimal.valueOf(0.0));
    }

    /**
     * Возвращает список всех записей за диапазон даты, время.
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @return Список записей
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingsByRange(RangeDataTimeDTO rangeDataTimeDTO) {
        List<Recording> recordings = recordingRepository.findByStartBetween(rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Отмечает запись, как завершенную, если она не завершена, по ее идентификатору.
     *
     * @param id Идентификатор записи
     */
    public void updateCompliteById(Integer id) {
        Recording recordingUpdate = recordingRepository.findByIdAndCompletedIsFalseAndRemovedIsFalseAndReservedIsTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id отсутствует или она уже завершенная"));
        recordingUpdate.setCompleted(true);
    }

    /**
     * Отменяет запись по ее идентификатору (удаляет, клиент больше не может ее подтвердить).
     *
     * @param id Идентификатор записи
     */
    public void cancellationReserveById(Integer id) {
        Recording recording = getRecordingDTOByIdNotRemovedAndNotCompleted(id);
        recording.setReserved(false);
        recording.setRemoved(true);
    }

    /**
     * Подтверждает запись по ее идентификатору.
     *
     * @param id Идентификатор записи
     */
    public void approve(Integer id) {
        Recording recording = recordingRepository.findByIdAndReservedIsFalseAndRemovedIsFalse(id)
                .orElseThrow(() -> new RecordingNotFoundException("Запись не найдена"));
        recording.setReserved(true);
    }

    /**
     * Создает запись
     *
     * @param recordingDTO Параметры записи
     * @return Идентификатор созданной записи
     */
    public Integer createRecordingByIdUser(RecordingCreateDTO recordingDTO) {
        User user = userService.getUserById(recordingDTO.idUser());
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
            log.info("У пользователя с id %d уже есть записи на это время".formatted(recordingDTO.idUser()));
            throw new IllegalArgumentException();
        }
        //Создаем запись
        Recording recording = new Recording();
        recording.setUser(user);
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(calculateCost(user, services));
        recording.setBox(accessibleBox.getBox());
        recording.setServices(services);
        return recordingRepository.save(recording).getId();
    }

    /**
     * Обновляет запись по ее идентификатору
     *
     * @param id           Идентификатор записи
     * @param recordingDTO Новые параметры записи
     */
    public void updateRecording(Integer id, RecordingCreateDTO recordingDTO) {
        Recording recording = getRecordingDTOByIdNotRemovedAndNotCompleted(id);
        User user = recording.getUser();
        Set<CarWashService> services = recordingDTO.idServices()
                .stream()
                .map(carWashServiceService::getServiceById)
                .collect(Collectors.toSet());
        RecordingInBox accessibleBox = getBoxRecord(services, recordingDTO, id, Action.UPDATE);
        //Проверяем, есть ли записи у юзера в это кроме той, что правим.
        LocalDateTime finishRecord = accessibleBox.getFinish();
        if (!recordingRepository.findByIdAndUserIdAndStartAndFinishAndRemovedIsFalse(user.getId(), recordingDTO.start(), finishRecord, id).isEmpty()) {
            log.info("У пользователя с id %d уже есть записи на это время".formatted(user.getId()));
            throw new IllegalArgumentException();
        }
        //Меняем
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(calculateCost(user, services));
        recording.setBox(accessibleBox.getBox());
        recording.setServices(services);
    }

    /**
     * Возвращает активные брони пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return Список активных броней
     */
    @Transactional(readOnly = true)
    public List<RecordingReservedDTO> getAllActiveReserveByIdUse(Integer userId) {
        userService.checkUserById(userId);
        return recordingRepository.findAllByUser_IdAndReservedIsTrueAndCompletedIsFalse(userId).stream().map(recording -> {
                    RecordingReservedDTO rsd = modelMapper.map(recording, RecordingReservedDTO.class);
                    rsd.setServicesName(recording.getServices().stream()
                            .map(CarWashService::getName)
                            .collect(Collectors.toSet()));
                    return rsd;
                })
                .toList();
    }

    /**
     * Возвращает список выполненных записей по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return Список выполненных записей
     */
    @Transactional(readOnly = true)
    public List<RecordingComplitedDTO> getAllComplitedRecordingByUserId(Integer userId) {
        List<Recording> recordings = recordingRepository.findAllByUser_IdAndCompletedIsTrue(userId);
        return recordings.stream()
                .map(r -> modelMapper.map(r, RecordingComplitedDTO.class))
                .toList();
    }

    /**
     * Возвращает список всех записей за диапазон даты времени по идентификатору бокса.
     *
     * @param rangeDataTimeDTO Диапазоны даты,
     * @param idBox            Идентификатор бокса
     * @return Список записей.
     */
    @Transactional(readOnly = true)
    public List<RecordingDTO> getAllRecordingsByRangeAndIdBox(RangeDataTimeDTO rangeDataTimeDTO, Integer idBox) {
        boxService.checkBoxById(idBox);
        List<Recording> recordings = recordingRepository.findByBox_IdAndStartBetween(idBox, rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
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
    public Recording getRecordingDTOByIdNotRemovedAndNotCompleted(Integer id) {
        return recordingRepository.findByIdAndRemovedIsFalseAndCompletedIsFalse(id)
                .orElseThrow(() -> new RecordingNotFoundException("запись c id %d отсутствует".formatted(id)));
    }

    private RecordingInBox getBoxRecord(Set<CarWashService> services, RecordingCreateDTO recordingDTO, Integer id, Action action){
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
            log.info("Свободное время отсутствует");
            throw new IllegalArgumentException();
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
        if (start.toLocalTime().isBefore(box.getStart())
                || finish.toLocalTime().isAfter(box.getFinish())) {
            return false;
        }
        switch (action) {
            case UPDATE -> {
                //Проверяем, есть ли другие записи в боксе в это время, не включая эту запись, т.к. она все равно будет перенесена
                if (!recordingRepository.findByIdAndBox_IdAndRemovedIsFalse(box.getId(), start, finish, id).isEmpty()) {
                    return false;
                }
            }
            case SAVE -> {
                //Проверяем, есть ли другие записи в боксе в это время
                if (!recordingRepository.findByBox_IdAndRemovedIsFalse(box.getId(), start, finish).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }


}
