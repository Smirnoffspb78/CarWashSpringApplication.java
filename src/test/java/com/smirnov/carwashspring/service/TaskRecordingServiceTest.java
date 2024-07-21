package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.repository.RecordingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TaskRecordingServiceTest {

    @Mock
    RecordingRepository recordingRepository;

    @InjectMocks
    TaskRecordingService taskRecordingService;

    @Test
    void checkRecords() {
        Recording recording = new Recording();
        recording.setId(1);
        recording.setRemoved(false);

        Recording recording2 = new Recording();
        recording.setId(2);
        recording.setRemoved(false);

        List<Recording> recordings = List.of(recording, recording2);

        doReturn(recordings).when(recordingRepository).findAllByBetween(LocalDateTime.now().minusMinutes(15), LocalDateTime.now().plusMinutes(10));
        taskRecordingService.checkRecords();
        assertEquals(recordings.get(0).isRemoved(), true);

    }
}