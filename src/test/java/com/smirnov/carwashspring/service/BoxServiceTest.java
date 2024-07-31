package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.AppTest;
import com.smirnov.carwashspring.dto.request.create.BoxCreateDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.repository.BoxRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static com.smirnov.carwashspring.AppTest.ILLEGAL_ID;
import static com.smirnov.carwashspring.AppTest.createBox;
import static com.smirnov.carwashspring.AppTest.createRecordingsDTO;
import static com.smirnov.carwashspring.AppTest.getUserWithId2;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    /**
     * Репозиторий бокса.
     */
    @Mock
    BoxRepository boxRepository;

    /**
     * Сервисный слой записей.
     */
    @Mock
    private RecordingService recordingService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BoxService boxService;

    @Test
    void save() {
        Box box = createBox();
        BoxCreateDTO boxCreateDTO = new BoxCreateDTO();

        doReturn(box).when(modelMapper).map(boxCreateDTO, Box.class);
        doReturn(box).when(boxRepository).save(box);

        assertEquals(box.getId(), boxService.save(boxCreateDTO));
    }

    @Test
    void updateBox_EntityNotFoundException() {
        BoxCreateDTO boxCreateDTO = new BoxCreateDTO();

        doReturn(false).when(boxRepository).existsById(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> boxService.updateBox(ILLEGAL_ID, boxCreateDTO));
    }

    @Test
    void updateBox_Void() {
        BoxCreateDTO boxCreateDTO = new BoxCreateDTO();
        Box box = createBox();

        doReturn(true).when(boxRepository).existsById(box.getId());
        doReturn(box).when(modelMapper).map(boxCreateDTO, Box.class);
        doReturn(box).when(boxRepository).save(box);

        boxService.updateBox(box.getId(), boxCreateDTO);
    }


    @Test
    void getAllRecordingById_ListRecordingsDTO() {
        Box box = createBox();
        List<RecordingDTO> recordingDTOS = createRecordingsDTO();
        final Integer boxId = box.getId();

        doReturn(Optional.of(box)).when(boxRepository).findById(box.getId());
        doReturn(recordingDTOS).when(recordingService).getRecordingDTOS(box.getRecordings());

        List<RecordingDTO> returnRecordingsDTO = boxService.getAllRecordingById(boxId, box.getUser().getId());
        assertEquals(returnRecordingsDTO.get(0).getId(), recordingDTOS.get(0).getId());
        assertEquals(returnRecordingsDTO.get(1).getId(), recordingDTOS.get(1).getId());
    }

    @Test
    void getAllBoxes_ListBoxes() {
        List<Box> boxes = List.of(createBox());

        doReturn(boxes).when(boxRepository).findAll();

        assertEquals(boxes, boxService.getAllBoxes());
    }

    @Test
    void getBoxById_BoxWithId1() {
        Box box = createBox();

        doReturn( Optional.of(box)).when(boxRepository).findById(box.getId());

        assertEquals(box, boxService.getBoxById(box.getId()));
    }

    @Test
    void getBoxById_EntityNotFoundException() {

        doReturn(Optional.empty()).when(boxRepository).findById(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> boxService.getBoxById(ILLEGAL_ID));
    }

    @Test
    void checkAccessOperator_ForbiddenAccessException_IllegalId() {
        Box box = createBox();

        assertEquals(box.getUser().getRole().getRolesUser().name(), RolesUser.ROLE_OPERATOR.name());
        assertThrows(ForbiddenAccessException.class, () -> boxService.checkAccessOperator(box, ILLEGAL_ID));
    }

    @Test
    void checkAccessOperator_void_NotOperator() {
        Box box = createBox();
        User user = getUserWithId2();
        box.setUser(user);

        boxService.checkAccessOperator(box, box.getUser().getId());
    }

    @Test
    void checkAccessOperator_void_OperatorWithLegalId() {
        Box box = createBox();

        boxService.checkAccessOperator(box, box.getUser().getId());
    }
}