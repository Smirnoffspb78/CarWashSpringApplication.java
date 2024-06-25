package com.smirnov.carwashspring.repository;

import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.exception.RecordingNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecordingRepositoryTest {

    @Autowired
    RecordingRepository repository;

    @Test
    void testInjection() {
        assertThat(repository).isNotNull();
    }

    @Test
    void findSumByRangeTest() {
        LocalDateTime start = LocalDateTime.of(2024, 5, 17, 12, 1);
        LocalDateTime finish = LocalDateTime.of(2024, 6, 1, 11, 59);
        assertEquals(repository.findSumByRange(start, finish).orElse(BigDecimal.ZERO), new BigDecimal(4000));
    }

    @Test
    void findByIdAndComplitedIsFalseAndReservedIsTrue_Test() {
        Recording recording = repository.findById(6).orElse(null);
        assertEquals(recording, repository.findByIdAndCompletedIsFalseAndRemovedIsFalseAndReservedIsTrue(6).orElseThrow(() -> new RecordingNotFoundException()));
    }

    @Test
    void findByIdAndComplitedIsFalseAndReservedIsTrue_ThrowsException() {
        assertThrows(RecordingNotFoundException.class, () -> repository.findByIdAndCompletedIsFalseAndRemovedIsFalseAndReservedIsTrue(1).orElseThrow(() -> new RecordingNotFoundException()));
    }

    @Test
    void findByIdAndRemovedIsFalseAndCompletedIsFalse_Test() {
        Recording recording = repository.findById(7).orElse(null);
        assertEquals(recording, repository.findByIdAndRemovedIsFalseAndCompletedIsFalse(7).orElseThrow(() -> new RecordingNotFoundException()));
    }

    @Test
    void findByIdAndRemovedIsFalseAndCompletedIsFalse_ThrowsException() {
        assertThrows(RecordingNotFoundException.class, () -> repository.findByIdAndCompletedIsFalseAndRemovedIsFalseAndReservedIsTrue(1).orElseThrow(() -> new RecordingNotFoundException()));
    }

    @Test
    void findByBox_Id_2() {
        List<Recording> recordings = List.of(repository.findById(3).orElse(null));
        assertEquals(recordings, repository.findByBox_Id(2));
    }

    @Test
    void findByBox_Id_1() {
        List<Recording> recordings = List.of(repository.findById(1).orElse(null),
                repository.findById(2).orElse(null),
                repository.findById(6).orElse(null),
                repository.findById(7).orElse(null),
                repository.findById(8).orElse(null));
        assertEquals(recordings, repository.findByBox_Id(1));
    }

    @Test
    void findByBox_Id_NoBox() {
        assertEquals(new ArrayList<>(), repository.findByBox_Id(125));
    }

    @Test
    void findByStartBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 5, 17, 12, 1);
        LocalDateTime finish = LocalDateTime.of(2024, 5, 18, 13, 59);
        List<Recording> recordings = List.of(
                repository.findById(2).orElse(null),
                repository.findById(4).orElse(null)
        );
        assertEquals( repository.findByStartBetween(start, finish), recordings);
    }

    @Test
    void findByBox_IdAndStartBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 5, 17, 12, 0);
        LocalDateTime finish = LocalDateTime.of(2024, 5, 18, 13, 59);
        List<Recording> recordings = List.of(
                repository.findById(1).orElse(null),
                repository.findById(2).orElse(null)
        );
        assertEquals(repository.findByBox_IdAndStartBetween(1, start, finish), recordings);
    }

    @Test
    void findByUserIdAndStartAndFinishAndRemovedIsFalse_Test() {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 11, 0);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 14, 0);
        List<Recording> recordings = List.of(
                repository.findById(7).get(),
                repository.findById(8).get()
        );
        assertEquals(repository.findByUserIdAndStartAndFinishAndRemovedIsFalse(1, start, finish), recordings);
    }

    @Test
    void findByUserIdAndStartAndFinishAndRemovedIsFalse_Test2() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 18, 14, 40);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 14, 0);
        List<Recording> recordings = List.of(
                repository.findById(7).get(),
                repository.findById(8).get()
        );
        assertEquals(repository.findByUserIdAndStartAndFinishAndRemovedIsFalse(1, start, finish), recordings);
    }

    @Test
    void findByUserIdAndStartAndFinishAndRemovedIsFalse_Test3() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 18, 14, 41);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 14, 0);
        List<Recording> recordings = List.of(
                repository.findById(8).get()
        );
        assertEquals(repository.findByUserIdAndStartAndFinishAndRemovedIsFalse(1, start, finish), recordings);
    }

    @Test
    void findByBox_IdAndReservedIsTrue_Test_box1() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 18, 14, 40);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 14, 0);
        List<Recording> recordings = List.of(
                repository.findById(7).get(),
                repository.findById(8).get()
        );
        assertEquals(repository.findByBox_IdAndRemovedIsFalse(1, start, finish), recordings);
    }

    @Test
    void findByBox_IdAndReservedIsTrue_Test2_box1() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 18, 14, 41);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 14, 0);
        List<Recording> recordings = List.of(
                repository.findById(8).get()
        );
        assertEquals(repository.findByBox_IdAndRemovedIsFalse(1, start, finish), recordings);
    }

    @Test
    void findByBox_IdAndReservedIsTrue_Test3_box1() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 18, 14, 41);
        LocalDateTime finish = LocalDateTime.of(2024, 8, 19, 13, 59);
        List<Recording> recordings = List.of(
        );
        assertEquals(repository.findByBox_IdAndRemovedIsFalse(1, start, finish), recordings);
    }


    @Test
    void findByBox_IdAndReservedIsTrue_Test_box2() {
        LocalDateTime start = LocalDateTime.of(2023, 8, 18, 14, 41);
        LocalDateTime finish = LocalDateTime.of(2025, 8, 19, 13, 59);
        List<Recording> recordings = List.of(
        );
        assertEquals(repository.findByBox_IdAndRemovedIsFalse(2, start, finish), recordings);
    }

    @Test
    void findAllByUser_IdAndReservedIsTrueAndCompletedIsFalse(){

    }


    @Test
    void findAllByUser_IdAndCompletedIsTrue() {
    }
}