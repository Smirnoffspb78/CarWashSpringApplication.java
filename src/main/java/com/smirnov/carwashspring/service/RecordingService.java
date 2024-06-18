package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.RangeTimeDTO;
import com.smirnov.carwashspring.dto.RecordingDTO;
import com.smirnov.carwashspring.entity.Box;
import com.smirnov.carwashspring.entity.Recording;
import com.smirnov.carwashspring.entity.Work;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.repository.RecordingRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Validated
public class RecordingService {

    /**
     * Репозиторий записи.
     */
    private final RecordingRepository recordingRepository;

    /**
     * Репозиторий бокса
     */
    private final BoxRepository boxRepository;

    /**
     * Подсчитывает выручку за заданный промежуток времени.
     *
     * @param start  Дата начала периода
     * @param finish Дата окончания перода
     * @return Выручка за заданный промежуток времени
     */
    @Transactional
    public BigDecimal getProfit(@NotNull(message = "start is null") LocalDateTime start,
                                @NotNull(message = "finish is null") LocalDateTime finish) {
        return recordingRepository.findSumByRange(start, finish).orElse(BigDecimal.valueOf(0.0));
    }

    @Transactional
    public void updateCompliteById(int id) {
        Recording recordingUpdate = recordingRepository.findByIdAndComplitedIsFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Записи с таким id остутсвует или она уже заврешенная"));
        recordingUpdate.setComplited(true);
    }


    /**
     * Вовзращает все записи бокса по его идентификатору.
     *
     * @param id дентификатор бокса
     * @return Список записей бокса
     */
    @Transactional
    public List<RecordingDTO> getAllRecordingsByIdBox(@NotNull Integer id) {
        checkBoxById(id);
        Box box = new Box();
        box.setId(id);
        List<Recording> recordings = recordingRepository.findAllByBox(box);
        return getRecordingDTOS(recordings);
    }

    /**
     * Вовзращает список всех записей за определенную дату, время.
     * @param rangeTimeDTO Диапазон даты, времени
     * @return Список записей
     */
    @Transactional
    public List<RecordingDTO> getAllRecordingsByRange(RangeTimeDTO rangeTimeDTO) {
        List<Recording> recordings = recordingRepository.findAllAndStartIsAfterAndStartIsBefore(rangeTimeDTO.start(), rangeTimeDTO.finish());
        return getRecordingDTOS(recordings);
    }

    /**
     * Вовзращает список всех записей за диапазон даты времени по идентификатору бокса.
     * @param rangeTimeDTO Диапазоны даты,
     * @param idBox Идентификатор бокса
     * @return Список записей.
     */
    @Transactional
    public List<RecordingDTO> getAllRecordingsByRangeAndIdBox(RangeTimeDTO rangeTimeDTO, Integer idBox) {
        checkBoxById(idBox);
        List<Recording> recordings = recordingRepository.findAllByBoxAndStartIsAfterAndStartIsBefore(rangeTimeDTO.start(), rangeTimeDTO.finish(), idBox);
        return getRecordingDTOS(recordings);
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
                                .map(Work::getId)
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
