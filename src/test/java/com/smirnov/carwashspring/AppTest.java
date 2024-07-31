package com.smirnov.carwashspring;

import com.smirnov.carwashspring.dto.range.RangeDataTimeDTO;
import com.smirnov.carwashspring.dto.response.get.CarWashServiceDTO;
import com.smirnov.carwashspring.dto.response.get.RecordingDTO;
import com.smirnov.carwashspring.entity.service.Box;
import com.smirnov.carwashspring.entity.service.CarWashService;
import com.smirnov.carwashspring.entity.service.Recording;
import com.smirnov.carwashspring.entity.users.Employee;
import com.smirnov.carwashspring.entity.users.Role;
import com.smirnov.carwashspring.entity.users.RolesUser;
import com.smirnov.carwashspring.entity.users.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public abstract class AppTest {

    public static final Integer ILLEGAL_ID = -1;

    public static final String LOGIN = "login";

    public static User getUserWithId2() {
        Recording recording = new Recording();
        recording.setId(1);
        Recording recording2 = new Recording();
        recording2.setId(2);

        User user = new User();
        user.setId(2);
        user.setDiscount(5);
        user.setLogin("login");
        user.setPassword("password");
        user.setRole(new Role(RolesUser.ROLE_USER));
        user.setRecordings(List.of(recording, recording2));
        return user;
    }

    public static Employee getEmployee() {
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

    public static Box createBox() {
        User user = new User();
        user.setId(21);
        user.setRole(new Role(RolesUser.ROLE_OPERATOR));
        Box box = new Box();
        box.setId(1);
        box.setUsageRate(1.5F);
        box.setUser(user);
        return box;
    }

    public static List<RecordingDTO> createRecordingsDTO(){
        RecordingDTO recordingDTO = new RecordingDTO();
        recordingDTO.setId(1);
        RecordingDTO recordingDTO2 = new RecordingDTO();
        recordingDTO2.setId(2);
        return List.of(recordingDTO, recordingDTO2);
    }

    public static CarWashServiceDTO getCarWashServiceDTO01(){
        CarWashServiceDTO carWashService = new CarWashServiceDTO();
        carWashService.setName("name");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    public static CarWashServiceDTO getCarWashServiceDTO02(){
        CarWashServiceDTO carWashService = new CarWashServiceDTO();
        carWashService.setName("name02");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    public static CarWashService getCarWashService01(){
        CarWashService carWashService = new CarWashService();
        carWashService.setId(1);
        carWashService.setName("name");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    public static CarWashService getCarWashService02(){
        CarWashService carWashService = new CarWashService();
        carWashService.setId(2);
        carWashService.setName("name02");
        carWashService.setTime(20);
        carWashService.setPrice(new BigDecimal(2000));
        return carWashService;
    }

    public static RangeDataTimeDTO createRangeDateTime(){
        RangeDataTimeDTO rangeDataTimeDTO = new RangeDataTimeDTO();
        rangeDataTimeDTO.setStart(LocalDateTime.now());
        rangeDataTimeDTO.setFinish(LocalDateTime.now().plusMonths(1));
        return rangeDataTimeDTO;
    }

    public static Recording createRecording() {
        Box box = new Box();
        box.setId(1);
        box.setUser(getEmployee());

        Recording recording = new Recording();
        recording.setId(1);
        recording.setUser(getUserWithId2());
        recording.setBox(box);

        return recording;
    }
}
