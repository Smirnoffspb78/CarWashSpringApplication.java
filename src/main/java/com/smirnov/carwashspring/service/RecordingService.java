package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingReservedDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingComplitedDTO;
import com.smirnov.carwashspring.email.EmailService;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.BoxNotFountException;
import com.smirnov.carwashspring.exception.RecordingNotFoundException;
import com.smirnov.carwashspring.repository.RecordingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecordingService {

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

    private final EmailService emailService;

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
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Список услуг не может быть пустым");
        }
        //Получаем базовое время выполнения всех услуг в записи
        int baseTimeAllWork = services.stream()
                .mapToInt(CarWashService::getTime)
                .sum();
        //Получаем все боксы
        List<Box> boxes = boxService.getAllBoxes();
        if (boxes.isEmpty()) {
            throw new BoxNotFountException("Отсутствуют боксы для записи");
        }
        //Вычисляем время, за которое запись будет выполнена в каждом боксе
        List<Integer> timeInBox = boxes.stream().map(box -> (int) ceil(box.getUsageRate() * baseTimeAllWork)).toList();
        //Формируем список всех доступных для записи боксов.
        List<Box> boxesAccessible = new ArrayList<>();
        boolean checkBox;
        for (int i = 0; i < boxes.size(); i++) {
            checkBox = true;
            LocalDateTime start = recordingDTO.start();
            LocalDateTime finish = recordingDTO.start().plusMinutes(timeInBox.get(i));
            //Проверяем не выпадаем ли на следующий день (круглосуточных боксов нет)
            if (finish.toLocalDate().isAfter(start.toLocalDate())) {
                checkBox = false;
            }
            //Проверяем работает ли бокс в эти часы
            if (!checkBox || start.toLocalTime().isBefore(boxes.get(i).getStart())
                    || finish.toLocalTime().isAfter(boxes.get(i).getFinish())) {
                checkBox = false;
            }
            //Проверяем, есть ли другие записи в боксе в это время
            if (!checkBox || !recordingRepository.findByBox_IdAndRemovedIsFalse(boxes.get(i).getId(), start, finish).isEmpty()) {
                checkBox = false;
            }
            //Пишем, если все проверки прошли
            if (checkBox) {
                boxesAccessible.add(boxes.get(i));
            }
        }
        if (boxesAccessible.isEmpty()) {
            log.info("Свободно время отсутствует");
            throw new IllegalArgumentException();
        }
        //Сортируем боксы от самого быстрого до самого медленного. Если в самом быстром запись пересекается с записями клиента, то дальше не смысла проверять
        boxesAccessible.sort(Comparator.comparingDouble(Box::getUsageRate));
        //Самое быстрое окончание выполнения записи
        LocalDateTime finishRecord = recordingDTO.start().plusMinutes((int) ceil(boxesAccessible.get(0).getUsageRate() * baseTimeAllWork));
        //Проверяем, есть ли записи у юзера в это время.
        if (!recordingRepository.findByUserIdAndStartAndFinishAndRemovedIsFalse(recordingDTO.idUser(), recordingDTO.start(), finishRecord).isEmpty()) {
            log.info("У пользователя с id %d уже есть записи на это время".formatted(recordingDTO.idUser()));
            throw new IllegalArgumentException();
        }
        //Получаем сумму всех услуг c учетом скидки
        BigDecimal discount = new BigDecimal(user.getDiscount());
        BigDecimal costWithDiscount = new BigDecimal(0);
        for (CarWashService service : services) {
            costWithDiscount = costWithDiscount.add(service.getPrice());
        }
        costWithDiscount = costWithDiscount.subtract(costWithDiscount.multiply(discount));
        //Мапим DTO в запись и сохраняем, возвращаем true
        Recording recording = new Recording();
        recording.setUser(user);
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(costWithDiscount);
        recording.setBox(boxesAccessible.get(0));
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
        //todo НАЧАЛО ОДИНАКОВОГО БЛОКА
        User user = userService.getUserById(recordingDTO.idUser());
        if (recording.getUser().getId().equals(recordingDTO.idUser())) {
            throw new RecordingNotFoundException("Это чужая запись");
        }
        Set<CarWashService> services = recordingDTO.idServices()
                .stream()
                .map(carWashServiceService::getServiceById)
                .collect(Collectors.toSet());
        //Получаем все боксы
        List<Box> boxes = boxService.getAllBoxes();
        if (boxes.isEmpty()) {
            throw new BoxNotFountException("Отсутствуют боксы для записи");
        }
        //Получаем базовое время выполнения всех услуг в записи
        int baseTimeAllWork = services.stream()
                .mapToInt(CarWashService::getTime)
                .sum();
        //Вычисляем время, за которое запись будет выполнена в каждом боксе
        List<Integer> timeInBox = boxes.stream().map(box -> (int) ceil(box.getUsageRate() * baseTimeAllWork)).toList();
        //Формируем список всех доступных для записи боксов.
        List<Box> boxesAccessible = new ArrayList<>();
        boolean checkBox;
        for (int i = 0; i < boxes.size(); i++) {
            checkBox = true;
            LocalDateTime start = recordingDTO.start();
            LocalDateTime finish = recordingDTO.start().plusMinutes(timeInBox.get(i));
            //Проверяем не выпадаем ли на следующий день (круглосуточных боксов нет)
            if (finish.toLocalDate().isAfter(start.toLocalDate())) {
                checkBox = false;
            }
            //Проверяем работает ли бокс в эти часы
            if (!checkBox || start.toLocalTime().isBefore(boxes.get(i).getStart())
                    || finish.toLocalTime().isAfter(boxes.get(i).getFinish())) {
                checkBox = false;
            }
            //todo окончание одинаковости


            //Проверяем, есть ли другие записи в боксе в это время, не включая эту запись, т.к. она все равно будет перенесена
            if (!checkBox || !recordingRepository.findByIdAndBox_IdAndRemovedIsFalse(boxes.get(i).getId(), start, finish, id).isEmpty()) {
                checkBox = false;
            }

            //todo Продолжение одинаковости
            //Пишем, если все проверки прошли
            if (checkBox) {
                boxesAccessible.add(boxes.get(i));
            }
        }
        if (boxesAccessible.isEmpty()) {
            log.info("Свободно время отсутствует");
            throw new IllegalArgumentException();
        }
        //Сортируем боксы от самого быстрого до самого медленного. Если в самом быстром запись пересекается с записями клиента, то дальше не смысла проверять
        boxesAccessible.sort(Comparator.comparingDouble(Box::getUsageRate));
        //Самое быстрое окончание выполнения записи
        LocalDateTime finishRecord = recordingDTO.start().plusMinutes((int) ceil(boxesAccessible.get(0).getUsageRate() * baseTimeAllWork));
        //todo окончание одинаковости
        //Проверяем, есть ли записи у юзера в это кроме кроме той, что правим.
        if (!recordingRepository.findByIdAndUserIdAndStartAndFinishAndRemovedIsFalse(recordingDTO.idUser(), recordingDTO.start(), finishRecord, id).isEmpty()) {
            log.info("У пользователя с id %d уже есть записи на это время".formatted(recordingDTO.idUser()));
            throw new IllegalArgumentException();
        }
        //todo Продолжение повторяемости
        //Получаем сумму всех услуг c учетом скидки
        BigDecimal discount = new BigDecimal(user.getDiscount());
        BigDecimal costWithDiscount = new BigDecimal(0);
        for (CarWashService service : services) {
            costWithDiscount = costWithDiscount.add(service.getPrice());
        }
        costWithDiscount = costWithDiscount.subtract(costWithDiscount.multiply(discount));
        //todo окончание повторяемости


        //Меняем
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(costWithDiscount);
        recording.setBox(boxesAccessible.get(0));
        recording.setServices(services);
        recordingRepository.save(recording);
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
        List<Recording> recordings = recordingRepository.findAllByUser_IdAndReservedIsTrueAndCompletedIsFalse(userId);
        return recordings.stream().map(
                        recording -> RecordingReservedDTO.builder()
                                .id(recording.getId())
                                .start(recording.getStart())
                                .finish(recording.getFinish())
                                .cost(recording.getCost())
                                .boxId(recording.getBox().getId())
                                .services(recording.getServices())
                                .build())
                .toList();
    }

    public void approve(Integer id) {
        Recording recording = recordingRepository.findByIdAndReservedIsFalseAndRemovedIsFalse(id)
                .orElseThrow(()->new RecordingNotFoundException("Запись не найдена"));
        recording.setReserved(true);
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
        return recordings.stream().map(r -> RecordingComplitedDTO.builder()
                        .id(r.getId())
                        .cost(r.getCost())
                        .timeComplite(Duration.between(r.getStart(), r.getFinish()).toMinutes())
                        .servicesName(r.getServices().stream()
                                .map(CarWashService::getName)
                                .collect(Collectors.toSet()))
                        .build())
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
}
