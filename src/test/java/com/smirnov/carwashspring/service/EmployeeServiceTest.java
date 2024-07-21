package com.smirnov.carwashspring.service;

import com.smirnov.carwashspring.dto.response.get.UserDetailsCustom;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;
import com.smirnov.carwashspring.repository.EmployeeRepository;
import com.smirnov.carwashspring.service.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

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
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void updateDiscountForUser_MinDiscount() {
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();

        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        int minDiscount = 8;
        String typeDiscount = "min";
        employeeService.updateDiscountForUser(employee.getId(), minDiscount, typeDiscount);

        assertEquals(minDiscount, employee.getMinDiscountForUsers());
    }

    @Test
    void updateDiscountForUser_MinDiscountThrow() {
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();

        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        int minDiscount = 21;
        String typeDiscount = "min";

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateDiscountForUser(employee.getId(), minDiscount, typeDiscount));
    }

    @Test
    void updateDiscountForUser_MaxDiscount() {
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();

        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        int maxDiscount = 21;
        String typeDiscount = "max";
        employeeService.updateDiscountForUser(employee.getId(), maxDiscount, typeDiscount);

        assertEquals(maxDiscount, employee.getMaxDiscountForUsers());
    }

    @Test
    void updateDiscountForUser_MaxDiscountThrow() {
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();

        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        int maxDiscount = 9;
        String typeDiscount = "max";

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateDiscountForUser(employee.getId(), maxDiscount, typeDiscount));
    }

    @Test
    void activateDiscount_ThrowIllegalArgumentForRightBorder() {
        User user = getUserWithId2();
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();
        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(employee.getName(), employee.getPassword(),
                List.of(new SimpleGrantedAuthority(employee.getRole().getRolesUser().name())), employee.getId());

        doReturn(user).when(userService).getUserById(2);
        doReturn(userDetailsCustom).when(jwtAuthenticationFilter).getAuthUser();
        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        assertThrows(IllegalArgumentException.class, () -> employeeService.activateDiscount(31, 2));
    }

    @Test
    void activateDiscount_ThrowIllegalArgumentForLeftBorder() {
        User user = getUserWithId2();
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();
        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(employee.getName(), employee.getPassword(),
                List.of(new SimpleGrantedAuthority(employee.getRole().getRolesUser().name())), employee.getId());

        doReturn(user).when(userService).getUserById(2);
        doReturn(userDetailsCustom).when(jwtAuthenticationFilter).getAuthUser();
        doReturn(optionalEmployee).when(employeeRepository).findById(1);

        assertThrows(IllegalArgumentException.class, () -> employeeService.activateDiscount(9, 2));
    }

    @Test
    void activateDiscount_ForUserWithId2() {
        User user = getUserWithId2();
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();
        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(employee.getName(), employee.getPassword(),
                List.of(new SimpleGrantedAuthority(employee.getRole().getRolesUser().name())), employee.getId());

        doReturn(user).when(userService).getUserById(user.getId());
        doReturn(userDetailsCustom).when(jwtAuthenticationFilter).getAuthUser();
        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());

        int discount = 10;
        employeeService.activateDiscount(discount, user.getId());
        assertEquals(user.getDiscount(), discount);
    }

    @Test
    void deactivateDiscount_ReturnUserDiscount0() {
        User user = getUserWithId2();
        doReturn(user).when(userService).getUserById(user.getId());
        employeeService.deactivateDiscount(user.getId());
        int zeroDiscount = 0;
        assertEquals(user.getDiscount(), zeroDiscount);
    }

    @Test
    void getEmployeeById_ReturnEmployeeId1() {
        Optional<Employee> optionalEmployee = Optional.of(getEmployee());
        Employee employee = optionalEmployee.orElseThrow();
        doReturn(optionalEmployee).when(employeeRepository).findById(employee.getId());


        Employee employeeTest = employeeService.getEmployeeById(employee.getId());
        assertEquals(employeeTest, optionalEmployee.orElseThrow());
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
        User user = new User();
        user.setId(2);
        user.setDiscount(5);
        return user;
    }
}