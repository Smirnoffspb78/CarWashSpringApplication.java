package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.AppTest;
import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.request.create.RecordingCreateDTO;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.repository.RecordingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.smirnov.carwashspring.AppTest.ILLEGAL_ID;
import static com.smirnov.carwashspring.AppTest.createRangeDateTime;
import static com.smirnov.carwashspring.AppTest.createRecording;
import static com.smirnov.carwashspring.AppTest.getEmployee;
import static com.smirnov.carwashspring.AppTest.getUserWithId2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RecordingServiceTest {

    /**
     * Репозиторий записи.
     */
    @Mock
    RecordingRepository recordingRepository;
    /**
     * Сервисный слой бокса.
     */
    @Mock
    BoxService boxService;
    /**
     * Сервисный слой пользователя.
     */
    @Mock
    UserService userService;
    /**
     * Сервисный слой услуг.
     */
    @Mock
    CarWashServiceService carWashServiceService;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    RecordingService recordingService;

    @Test
    void getProfit() {
        final BigDecimal profit = new BigDecimal(1000);
        final RangeDataTimeDTO rangeDataTimeDTO = createRangeDateTime();

        doReturn(Optional.of(profit)).when(recordingRepository).findSumByRange(rangeDataTimeDTO.getStart(), rangeDataTimeDTO.getFinish());

        assertEquals(profit, recordingService.getProfit(rangeDataTimeDTO));
    }

    @Test
    void getProfit_Null() {
        final RangeDataTimeDTO rangeDataTimeDTO = createRangeDateTime();

        doReturn(Optional.empty()).when(recordingRepository).findSumByRange(rangeDataTimeDTO.getStart(), rangeDataTimeDTO.getFinish());

        assertEquals(BigDecimal.valueOf(0.0), recordingService.getProfit(rangeDataTimeDTO));
    }

    @Test
    void getAllRecordingsByRange() {
    }

    @Test
    void cancellationReserveById_EntityNotFountException() {
        User user = getUserWithId2();

        doReturn(Optional.empty()).when(recordingRepository).findByIdAndRemovedIsFalseAndCompletedIsFalse(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> recordingService.cancellationReserveById(ILLEGAL_ID, user.getId()));
    }

    @Test
    void cancellationReserveById_ForbiddenAccessException() {
        User user = getUserWithId2();
        User userIllegal = new User();
        userIllegal.setId(21);
        userIllegal.setRole(new Role(RolesUser.ROLE_USER));

        Recording recording = new Recording();
        recording.setId(1);
        recording.setReserved(true);
        recording.setArrived(true);
        recording.setRemoved(false);
        recording.setUser(user);
        Integer recordingId = recording.getId();

        doReturn(Optional.of(recording)).when(recordingRepository).findByIdAndRemovedIsFalseAndCompletedIsFalse(recordingId);
        doReturn(userIllegal).when(userService).getUserById(ILLEGAL_ID);

        assertThrows(ForbiddenAccessException.class, () -> recordingService.cancellationReserveById(recordingId, ILLEGAL_ID));
    }



    @Test
    void approve_EntityNotFoundException() {
        doReturn(Optional.empty()).when(recordingRepository).findByIdAndRemovedIsFalseAndReservedIsFalse(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> recordingService.approve(ILLEGAL_ID));
    }

    @Test
    void approve_void() {
        Recording recording = createRecording();
        Integer recordingId = recording.getId();

        doReturn(Optional.of(recording)).when(recordingRepository).findByIdAndRemovedIsFalseAndReservedIsFalse(recordingId);
        recordingService.approve(recordingId);

        assertTrue(recording.isReserved());
    }

    @Test
    void arrive_EntityNotFoundException() {
        doReturn(Optional.empty()).when(recordingRepository).findByIdAndReservedIsTrueAndArrivedIsFalse(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> recordingService.arrive(ILLEGAL_ID, ILLEGAL_ID));
    }

    @Test
    void arrive_void() {;
        Recording recording = createRecording();

        doReturn(Optional.of(recording)).when(recordingRepository).findByIdAndReservedIsTrueAndArrivedIsFalse(recording.getId());
        recordingService.arrive(recording.getId(), recording.getUser().getId());

        assertTrue(recording.isArrived());
    }


    @Test
    void updateCompleteById_EntityNotFoundException() {
        doReturn(Optional.empty()).when(recordingRepository).findByIdAndArrivedIsTrueAndCompletedIsFalse(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, ()-> recordingService.updateCompleteById(ILLEGAL_ID, ILLEGAL_ID));
    }

    @Test
    void updateCompleteById_void() {
        Recording recording = createRecording();
        Integer operatorId = recording.getBox().getUser().getId();

        doReturn(Optional.of(recording)).when(recordingRepository).findByIdAndArrivedIsTrueAndCompletedIsFalse(recording.getId());
        doNothing().when(boxService).checkAccessOperator(recording.getBox(), operatorId);
        recordingService.updateCompleteById(recording.getId(), operatorId);

        assertTrue(recording.isCompleted());
    }

    @Test
    void updateRecording() {
    }

    @Test
    void getAllActiveReserveByUserId() {
    }

    @Test
    void getAllCompletedRecordingByUserId() {
    }

    @Test
    void getAllRecordingsByRangeAndIdBox() {
    }

    @Test
    void getRecordingDTOS() {
    }

    @Test
    void getRecordingByIdNotRemovedAndNotCompleted() {
    }

    @Test
    void getAllRecordingsUser() {
    }


}