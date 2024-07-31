package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.enums.TypeDiscount;
import com.smirnov.carwashspring.exception.EntityNotFoundException;
import com.smirnov.carwashspring.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.smirnov.carwashspring.AppTest.ILLEGAL_ID;
import static com.smirnov.carwashspring.AppTest.getEmployee;
import static com.smirnov.carwashspring.AppTest.getUserWithId2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    /**
     * Репозиторий работников.
     */
    @Mock
    private EmployeeRepository employeeRepository;

    /**
     * Сервисный слой пользователей.
     */
    @Mock
    private UserService userService;

    private static Stream<Arguments> validMinMax() {
        return Stream.of(
                Arguments.of(TypeDiscount.MIN, 8),
                Arguments.of(TypeDiscount.MAX, 21)
        );
    }

    private static Stream<Arguments> invalidMinMax() {
        return Stream.of(
                Arguments.of(TypeDiscount.MIN, 21),
                Arguments.of(TypeDiscount.MAX, 9)
        );
    }

    @ParameterizedTest
    @MethodSource("validMinMax")
    void updateDiscountForUser_MinDiscount(TypeDiscount typeDiscount, int discount) {
        Employee employee = getEmployee();

        doReturn(Optional.of(employee)).when(employeeRepository).findById(employee.getId());

        employeeService.updateDiscountForUser(employee.getId(), discount, typeDiscount);

        switch (typeDiscount){
            case MIN -> assertEquals(discount, employee.getMinDiscountForUsers());
            case MAX -> assertEquals(discount, employee.getMaxDiscountForUsers());
        }
    }

    @ParameterizedTest
    @MethodSource("invalidMinMax")
    void updateDiscountForUser_MinMaxDiscount_IllegalArgumentException(TypeDiscount typeDiscount, int discount) {
        Employee employee = getEmployee();

        doReturn(Optional.of(employee)).when(employeeRepository).findById(employee.getId());

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateDiscountForUser(employee.getId(), discount, typeDiscount));;
    }

    @ParameterizedTest
    @ValueSource(ints = {31, 9})
    void activateDiscount_ThrowIllegalArgumentForRightLeftBorder(int discount) {
        User user = getUserWithId2();
        Employee employee = getEmployee();
        Integer userId = user.getId();
        Integer employeeId = employee.getId();

        doReturn(user).when(userService).getUserById(userId);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(employeeId);

        assertThrows(IllegalArgumentException.class, () -> employeeService.activateDiscount(discount, userId, employeeId));
    }

    @Test
    void activateDiscount_ForUserWithId2() {
        User user = getUserWithId2();
        Employee employee = getEmployee();
        Integer userId = user.getId();
        Integer employeeId = employee.getId();
        final int discount = 10;

        doReturn(user).when(userService).getUserById(userId);
        doReturn(Optional.of(employee)).when(employeeRepository).findById(employeeId);

        employeeService.activateDiscount(discount, userId, employeeId);
        assertEquals(user.getDiscount(), discount);
    }

    @Test
    void deactivateDiscount_ReturnUserDiscount0() {
        User user = getUserWithId2();
        final int zeroDiscount = 0;

        doReturn(user).when(userService).getUserById(user.getId());
        employeeService.deactivateDiscount(user.getId());

        assertEquals(user.getDiscount(), zeroDiscount);
    }

    @Test
    void getEmployeeById_ReturnEmployeeId1() {
        Employee employee = getEmployee();
        Integer employeeId = employee.getId();

        doReturn(Optional.of(employee)).when(employeeRepository).findById(employeeId);
        Employee employeeTest = employeeService.getEmployeeById(employeeId);

        assertEquals(employeeTest, employee);
    }

    @Test
    void getEmployeeById_EntityNotFoundException() {

        doReturn(Optional.empty()).when(employeeRepository).findById(ILLEGAL_ID);

        assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployeeById(ILLEGAL_ID));
    }
}