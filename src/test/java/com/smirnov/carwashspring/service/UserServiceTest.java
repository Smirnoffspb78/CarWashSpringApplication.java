package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.ForbiddenAccessException;
import com.smirnov.carwashspring.exception.LoginException;
import com.smirnov.carwashspring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.smirnov.carwashspring.AppTest.ILLEGAL_ID;
import static com.smirnov.carwashspring.AppTest.LOGIN;
import static com.smirnov.carwashspring.AppTest.getUserWithId2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    RecordingService recordingService;

    @InjectMocks
    UserService userService;


    @Test
    void updateUserBeforeOperator() {
        User user = getUserWithId2();
        Employee employee = generateEmployeeByUser(user);
        Recording recording = new Recording();
        recording.setId(1);
        recording.setUser(user);
        Recording recording2 = new Recording();
        recording2.setId(2);
        recording2.setUser(user);
        List<Recording> recordings = List.of(recording, recording2);

        doReturn(Optional.of(user)).when(userRepository).findByIdAndRoleAndDeletedIsFalse(user.getId(), user.getRole());
        doReturn(employee).when(modelMapper).map(user, Employee.class);
        doReturn(employee).when(userRepository).save(employee);
        doReturn(recordings).when(recordingService).getAllRecordingsUser(user);

        userService.updateUserBeforeOperator(user.getId());
        assertTrue(user.isDeleted());
    }

    @Test
    void checkUserByLogin_LoginException() {
        doReturn(true).when(userRepository).existsByLoginAndDeletedIsFalse(LOGIN);

        assertThrows(LoginException.class, ()-> userService.checkUserByLogin(LOGIN));
    }

    @Test
    void checkUserByLogin() {
        doReturn(false).when(userRepository).existsByLoginAndDeletedIsFalse(LOGIN);

        userService.checkUserByLogin(LOGIN);
    }

    @Test
    void saveUser() {
        User user = getUserWithId2();

        doReturn(user).when(userRepository).save(user);

        assertEquals(user.getId(), userService.saveUser(user));
    }

    @Test
    void deleteUser_ForbiddenAccessException() {
        User user = getUserWithId2();

        assertThrows(ForbiddenAccessException.class, () -> userService.deleteUser(user.getId(), ILLEGAL_ID));
    }

    @Test
    void deleteUser_Void() {
        User user = getUserWithId2();
        Integer userId = user.getId();

        doReturn(Optional.of(user)).when(userRepository).findByIdAndDeletedIsFalse(user.getId());

        userService.deleteUser(userId, userId);
        assertTrue(user.isDeleted());
    }

    @Test
    void getUserById() {
        User user = getUserWithId2();

        doReturn(Optional.of(user)).when(userRepository).findByIdAndDeletedIsFalse(user.getId());

        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void getUserById_EntityNotFoundException() {
        doReturn(Optional.empty()).when(userRepository).findByIdAndDeletedIsFalse(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(ILLEGAL_ID));
    }

    @Test
    void checkUserById_ReturnThrowEntityNotFoundException() {
        doReturn(false).when(userRepository).existsById(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> userService.checkUserById(ILLEGAL_ID));
    }

    @Test
    void checkUserById_Void() {
        final Integer id = 1;

        doReturn(true).when(userRepository).existsById(id);

        userService.checkUserById(id);
    }

    @Test
    void loadUserByUsername_UsernameNotFoundException() {
        doReturn(Optional.empty()).when(userRepository).findByLoginAndDeletedIsFalse(LOGIN);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(LOGIN));
    }

    @Test
    void loadUserByUsername() {
        User user = getUserWithId2();

        doReturn(Optional.of(user)).when(userRepository).findByLoginAndDeletedIsFalse(user.getLogin());
        UserDetailsCustom userDetailsCustomTest = userService.loadUserByUsername(user.getLogin());

        assertEquals(userDetailsCustomTest.getId(), user.getId());
        assertEquals(userDetailsCustomTest.getUsername(), user.getLogin());
        assertEquals(userDetailsCustomTest.getPassword(), user.getPassword());
        assertEquals(userDetailsCustomTest.getRolesUser(),user.getRole().getRolesUser());
    }

    private Employee generateEmployeeByUser(User user){
        Employee employee = new Employee();
        employee.setId(user.getId());
        employee.setDiscount(user.getDiscount());
        employee.setRole(new Role(RolesUser.ROLE_OPERATOR));
        employee.setRecordings(user.getRecordings());
        return employee;
    }
}