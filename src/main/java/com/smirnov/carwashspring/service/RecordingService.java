package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.RecordingDTO;
import com.smirnov.carwashspring.dto.RecordingForUserDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.exception.UserNotFoundException;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.repository.RecordingRepository;
import com.smirnov.carwashspring.repository.UserRepository;
import com.smirnov.carwashspring.repository.CarWashServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.ceil;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordingService {

    /**
     * Репозиторий записи.
     */
    private final RecordingRepository recordingRepository;

    /**
     * Список неподтвержденных записей
     */
    private List<Recording> recordingsNotApproved = new ArrayList<>(); //todo потокобезопасный лист. Стэк не подйодет, т.к. удаляться могут в произвольном порядке
                                                                        //todo удаление происходит через отдельный метод

    /**
     * Репозиторий бокса
     */
    private final BoxRepository boxRepository;
    /**
     * Репозиторий пользователя
     */
    private final UserRepository userRepository;
    private final CarWashServiceRepository carWashServiceRepository;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param rangeDataTimeDTO диапаизон даты, времени
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
        Recording recordingUpdate = recordingRepository.findByIdAndComplitedIsFalseAndReservedIsTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id остутсвует или она уже заврешенная"));
        recordingUpdate.setReserved(false);
        recordingUpdate.setComplited(true);
    }


    /**
     * Вовзращает все записи бокса по его идентификатору.
     *
     * @param id Идентификатор бокса
     * @return Список записей бокса
     */
    public List<RecordingDTO> getAllRecordingsByIdBox(Integer id) {
        checkBoxById(id);
        List<Recording> recordings = recordingRepository.findAllByBox_Id(id);
        return getRecordingDTOS(recordings);
    }

    /**
     * Вовзращает список всех записей за определенную дату, время.
     *
     * @param rangeDataTimeDTO Диапазон даты, времени
     * @return Список записей
     */
    public List<RecordingDTO> getAllRecordingsByRange(RangeDataTimeDTO rangeDataTimeDTO) {
        List<Recording> recordings = recordingRepository.findAllByStartBetween(rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Вовзращает список всех записей за диапазон даты времени по идентификатору бокса.
     *
     * @param rangeDataTimeDTO Диапазоны даты,
     * @param idBox            Идентификатор бокса
     * @return Список записей.
     */
    public List<RecordingDTO> getAllRecordingsByRangeAndIdBox(RangeDataTimeDTO rangeDataTimeDTO, Integer idBox) {
        checkBoxById(idBox);
        List<Recording> recordings = recordingRepository.findAllByBox_IdAndStartBetween(idBox, rangeDataTimeDTO.start(), rangeDataTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Снимает бронь с записи (делает ее отмененной) по ее идентифкатору.
     *
     * @param id Идентификатор записи
     */
    public void cancellationReserveById(Integer id) {
        Recording recording = recordingRepository.findByIdAndReservedIsTrue(id).orElseThrow(() -> new IllegalArgumentException("записью с бронью c id %d отсутствует".formatted(id)));
        recording.setReserved(false);
    }

    public boolean createRecordingByIdUser(RecordingCreateDTO recordingDTO) {
        if (!userRepository.existsById(recordingDTO.idUser())) {
            throw new IllegalArgumentException("user not found");
        }
        //Получаем базовое время выполнения
        int baseTimeAllWork = recordingDTO.idServices().stream()
                .mapToInt(id -> carWashServiceRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Услуги с id %d не найдено".formatted(id))).getTime())
                .sum();
        //Получаем все боксы
        List<Box> boxes = boxRepository.findAll();
        if (boxes.isEmpty()) {
            throw new IllegalArgumentException("Отсутсвуют боксы для записи");
        }
        //Получаем, как эта запись будет выполняться в каждом боксе с учетом коэффициента использования при условии, что бокс работает и в нем есть свободные места
        //Map<Integer, List<LocalDateTime>> rangeTimeInBox = new HashMap<>();
        List<Box> accessibleBoxes = boxes.stream()
                //Фильтруем боксы, у которых окончание работ на следующий день
                .filter(box -> !recordingDTO.start().plusMinutes((int) ceil(baseTimeAllWork * box.getUsageRate())).toLocalDate().isAfter(recordingDTO.start().toLocalDate()))
                //Фильтруем боксы, которые не работают в это время
                .filter(box -> !recordingDTO.start().toLocalTime().isBefore(box.getStart())
                        && !recordingDTO.start().toLocalTime().isAfter(box.getFinish())
                        && !recordingDTO.start().plusMinutes((int) ceil(baseTimeAllWork * box.getUsageRate())).toLocalTime().isAfter(box.getFinish()))
                //филбтруем боксы, у которых есть записи в это время
                .filter(box -> /*проверка наличия записей в этом диапазоне времени методом count из БД с помощью JOIN запроса
                 если количество не ноль, значит есть записи и попасть невохможно*/ box != null)
                .sorted(/*сортируем по коэффициенту работы и получаем первый. Если в первый не удается записаться, то на больший промежуток времени точно не удасттся записаться*/)
                .toList();
        if (accessibleBoxes.isEmpty()) {
            return false;
        }
        //Идем по списку боксов. Если Встречаем бокс, который не пересекается с нашей щаписью, то идем в него
        List<Recording> recordingsUser = recordingRepository.findAllByUserAndStartAndFinishAndReservedIsTrueAndComplitedIsFalse(
                recordingDTO.idUser(), recordingDTO.start(), recordingDTO.start().plusMinutes(40));
        System.out.println(LocalDateTime.now() + "Время" + recordingsUser);


        //recordingsNotApproved.add(reocrding) todo задача должна добавляться в список неподтвержденных задач
        return true;
    }

    /**
     * Возвращает активные брони пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return Список активных броней
     */
    @Transactional
    public List<RecordingForUserDTO> getAllActiveReserveByIdUse(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с id %d не найден".formatted(userId));
        }
        List<Recording> recordings = recordingRepository.findAllByUser_IdAndReservedIsTrue(userId);
        return recordings.stream().map(
                        recording -> RecordingForUserDTO.builder()
                                .id(recording.getId())
                                .start(recording.getStart())
                                .finish(recording.getFinish())
                                .cost(recording.getCost())
                                .boxId(recording.getBox().getId())
                                .services(recording.getServices())
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
            throw new IllegalArgumentException("box с таким id не существует");
        }
    }
}
