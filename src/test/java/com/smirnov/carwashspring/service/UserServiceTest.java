package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.exception.LoginException;
import com.smirnov.carwashspring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserService userService;

    @Test
    void updateUserBeforeOperator() {
        Optional<User> userOptional = Optional.of(getUserWithId2());
        User user = userOptional.orElseThrow();
        Employee employee = generateEmployeeByUser(user);

        doReturn(userOptional).when(userRepository).findByIdAndRoleAndDeletedIsFalse(user.getId(), user.getRole());
        doReturn(employee).when(modelMapper).map(user, Employee.class);
        doReturn(employee).when(userRepository).save(employee);

        userService.updateUserBeforeOperator(user.getId());
        boolean deleteUser = true;
        assertEquals(user.isDeleted(), deleteUser);
    }

    @Test
    void checkUserByLogin() {
        String login = "login";
        doReturn(true).when(userRepository).existsByLoginAndDeletedIsFalse(login);
        assertThrows(LoginException.class, ()-> userService.checkUserByLogin(login));
    }

    @Test
    void saveUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void checkUserById_ReturnThrowEntityNotFoundException() {
        Integer id = 1;
        doReturn(false).when(userRepository).existsById(id);
        assertThrows(EntityNotFoundException.class, () -> userService.checkUserById(id));
    }

    @Test
    void loadUserByUsername() {
    }

    private Employee getEmployee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setLogin("login");
        employee.setPassword("123456789");
        employee.setName("name");
        employee.setRole(new Role(RolesUser.ROLE_OPERATOR));
        employee.setMinDiscountForUsers(10);
        employee.setMaxDiscountForUsers(20);
        return employee;
    }

    private User getUserWithId2() {
        Recording recording = new Recording();
        recording.setId(1);
        Recording recording2 = new Recording();
        recording2.setId(2);

        User user = new User();
        user.setId(2);
        user.setDiscount(5);
        user.setRole(new Role(RolesUser.ROLE_USER));
        user.setRecordings(List.of(recording, recording2));
        return user;
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