package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.*;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.BoxNotFountException;
import com.smirnov.carwashspring.exception.RecordingNotFoundException;
import com.smirnov.carwashspring.exception.ServiceNotFoundException;
import com.smirnov.carwashspring.exception.UserNotFoundException;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.repository.RecordingRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import com.smirnov.carwashspring.repository.CarWashServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BoxRepository boxRepository;
    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий услуг.
     */
    private final CarWashServiceRepository carWashServiceRepository;

    /**
     * Список неподтвержденных записей
     */
    private final Set<Recording> recordingsNotApproved = new HashSet<>(); //todo потокобезопасный лист. Стэк не подйодет, т.к. удаляться могут в произвольном порядке
    //todo удаление происходит через отдельный метод

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param rangeDataTimeDTO диапазон даты, времени
     * @return Выручка за заданный промежуток времени
     */
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
        Recording recordingUpdate = recordingRepository.findByIdAndComplitedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id отсутствует или она уже завершенная"));
        recordingUpdate.setReserved(false);
        recordingUpdate.setComplited(true);
    }


    /**
     * Возвращает все записи бокса по его идентификатору.
     *
     * @param id Идентификатор бокса
     * @return Список записей бокса
     */
    public List<RecordingDTO> getAllRecordingsByIdBox(Integer id) {
        checkBoxById(id);
        List<Recording> recordings = recordingRepository.findByBox_Id(id);
        return getRecordingDTOS(recordings);
    }

    /**
     * Возвращает список всех записей за определенную дату, время.
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @return Список записей
     */
    public List<RecordingDTO> getAllRecordingsByRange(RangeDataTimeDTO rangeDataTimeDTO) {
        List<Recording> recordings = recordingRepository.findByStartBetween(rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Возвращает список всех записей за диапазон даты времени по идентификатору бокса.
     *
     * @param rangeDataTimeDTO Диапазоны даты,
     * @param idBox            Идентификатор бокса
     * @return Список записей.
     */
    public List<RecordingDTO> getAllRecordingsByRangeAndIdBox(RangeDataTimeDTO rangeDataTimeDTO, Integer idBox) {
        checkBoxById(idBox);
        List<Recording> recordings = recordingRepository.findByBox_IdAndStartBetween(idBox, rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Снимает бронь с записи (делает ее отмененной) по ее идентификатору.
     *
     * @param id Идентификатор записи
     */
    public void cancellationReserveById(Integer id) {
        Recording recording = recordingRepository.findByIdAndReservedIsTrue(id).orElseThrow(() -> new IllegalArgumentException("записью с бронью c id %d отсутствует".formatted(id)));
        recording.setReserved(false);
    }

    public boolean createRecordingByIdUser(RecordingCreateDTO recordingDTO) {
        User user = userRepository.findById(recordingDTO.idUser())
                .orElseThrow(()-> new UserNotFoundException("Пользователь с id %d не найден".formatted(recordingDTO.idUser())));
        Set<CarWashService> services= recordingDTO.idServices()
                .stream().map(id -> carWashServiceRepository.findById(id)
                        .orElseThrow(() -> new ServiceNotFoundException("Услуги с id %d не найдено".formatted(id))))
                .collect(Collectors.toSet());
        //Получаем базовое время выполнения всех услуг в записи
        int baseTimeAllWork = services.stream()
                .mapToInt(CarWashService::getTime)
                .sum();
        //Получаем все боксы
        List<Box> boxes = boxRepository.findAll();
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("Отсутствуют боксы для записи");
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
                    && start.toLocalTime().isAfter(boxes.get(i).getFinish())
                    && finish.toLocalTime().isAfter(boxes.get(i).getFinish())) {
                checkBox = false;
            }
            //Проверяем, есть ли другие записи в боксе в это время
            if (!checkBox || !recordingRepository.findByBox_IdAndReservedIsTrue(boxes.get(i).getId(), start, finish).isEmpty()){
                checkBox = false;
            }
            //Пишем, если все проверки прошли
            if (checkBox){
                boxesAccessible.add(boxes.get(i));
            }
        }
        if (boxesAccessible.isEmpty()) {
            log.info("Свободно время отсутствует");
            return false;
        }
        //Сортируем боксы от самого быстрого до самого медленного. Если в самом быстром запись пересекается с записями клиента, то дальше не смысла проверять
        boxesAccessible.sort(Comparator.comparingDouble(Box::getUsageRate));
        //Самое быстрое окончание выполнения записи
        LocalDateTime finishRecord = recordingDTO.start().plusMinutes((int) ceil(boxesAccessible.get(0).getUsageRate() * baseTimeAllWork));
        //Проверяем, есть ли записи у юзера в это время.
        if (!recordingRepository.findByUserIdAndStartAndFinishAndReservedIsTrue(recordingDTO.idUser(), recordingDTO.start(), finishRecord).isEmpty()){
            log.info("У пользователя с id %d уже есть записи на это время".formatted(recordingDTO.idUser()));
            return false;
        }
        //Получаем сумму всех услуг c учетом скидки
        BigDecimal discount = new BigDecimal(user.getDiscount());
        BigDecimal costWithDiscount = new BigDecimal(0);
        for (CarWashService service : services) {
            costWithDiscount = costWithDiscount.add(service.getPrice());
            System.out.println(service.getPrice());
            System.out.println(costWithDiscount);
        }
        costWithDiscount = costWithDiscount.subtract(costWithDiscount.multiply(discount)) ;
        System.out.println("Стоимость услуг "+costWithDiscount);
        //Мапим DTO в запись и сохраняем, возвращаем true
        user.setId(recordingDTO.idUser());
        Recording recording = new Recording();
        recording.setUser(user);
        recording.setStart(recordingDTO.start());
        recording.setFinish(finishRecord);
        recording.setCost(costWithDiscount);
        recording.setBox(boxesAccessible.get(0));
        recording.setServices(services);
        //Добавляем в список неподтвержденных задач и сохраняем в БД
        recordingsNotApproved.add(recording);
        recordingRepository.save(recording);
        return true;
    }

    /**
     * Возвращает активные брони пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return Список активных броней
     */
    public List<RecordingReservedDTO> getAllActiveReserveByIdUse(Integer userId) {
       /* checkUserById(userId);*/
        List<Recording> recordings = recordingRepository.findAllByUser_IdAndReservedIsTrue(userId);
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

    /**
     * Возвращает список выполненных записей по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return Список выполненных записей
     */
    public List<RecordingComplitedDTO> getAllComplitedRecordingByUserId(Integer userId) {
        List<Recording> recordings = recordingRepository.findAllByUser_idAndComplitedIsTrue(userId);
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




    private List<RecordingDTO> getRecordingDTOS(List<Recording> recordings) {
        return recordings.stream().map(
                recording -> RecordingDTO.builder()
                        .id(recording.getId())
                        .idUser(recording.getUser().getId())
                        .start(recording.getStart())
                        .finish(recording.getFinish())
                        .reserved(recording.isReserved())
                        .complited(recording.isComplited())
                        .cost(recording.getCost())
                        .idBox(recording.getBox().getId())
                        .idServices(recording.getServices()
                                .stream()
                                .map(CarWashService::getId)
                                .collect(Collectors
                                        .toSet()))
                        .build()).toList();
    }

    private void checkBoxById(Integer id) {
        if (!boxRepository.existsById(id)) {
            throw new BoxNotFountException("Бокс с id "+id+" не найден");
        }
    }

    /*private void checkUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с id %d не найден".formatted(userId));
        }
    }*/

    /**
     * Удаляет запись из списка неподтвержденных записей
     * @param id Идентификатор записи.
     */
    private void approveRecordingById(Integer id) {
        Recording recording = recordingRepository.findById(id).
                orElseThrow(()-> new RecordingNotFoundException("Запись с id "+ id +" подтвреждена или не существует" ));
    recordingsNotApproved.remove(recording);
    }
}
