package com.smirnov.carwashspring;

import com.smirnov.carwashspring.dto.request.create.UserCreateDTO;
import com.smirnov.carwashspring.entity.users.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.ceil;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
@Component
public class testPattern {


    public static void main(String[] args) {
       /* Pattern pattern = Pattern.compile("[A-ZА-Я][A-zА-я-]{0,199}");



        Matcher matcher = pattern.matcher("А- я");
        System.out.println(matcher.matches());
*/
        int a = 40;
        double b = a * 1.17;
        System.out.println(b);
        System.out.println(ceil(b));

        A a1 = new A();
        a1.outE();

        UserCreateDTO userCreateDTO = new UserCreateDTO("Login", "password111", "testname", "testEmail");
        System.out.println(userCreateDTO);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);


        Pattern pattern2 = Pattern.compile("max|min");//
        Matcher matcher2 = pattern2.matcher("min");
        System.out.println("Тест пароля "+matcher2.matches());


        /*modelMapper.typeMap(BoxCreateDTO.class, BoxTest.class)
                .addMappings(mapping -> mapping.using((MappingContext<Integer, User> context) -> {
                    final Integer userId = context.getSource();
                    User user = new User();
                    user.setId(userId);
                    return user;
                }).map(BoxCreateDTO::userId, BoxTest::setUser));

*/
        boolean completed = true;
        if (completed) {
            System.out.println(userCreateDTO);
            completed = false;
        }
        System.out.println(modelMapper.map(userCreateDTO, User.class));




       /* BoxCreateDTO boxCreateDTO =
                new BoxCreateDTO(LocalTime.now(), LocalTime.now().plusMinutes(40), 1.1F, 1);*/

        //Box box = modelMapper.map(boxCreateDTO, Box.class);




        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTime1= LocalDateTime.now().plusMinutes(2);
        System.out.println(Duration.between(localDateTime, localDateTime1).toMinutes());




    }
}

class A {
    @AllArgsConstructor
    @Getter
    private enum TypeDiscount{
        MIN_DISCOUNT("min"),
        MAX_DISCOUNT("max");
        private final String name;
    }

    public void outE(){
        System.out.println(TypeDiscount.valueOf("MIN_DISCOUNT"));
    }

}

@NoArgsConstructor
@Getter
@Setter
@Validated
@Service
class B {
    private A a;

    public B(A a) {
        this.a = a;
    }
    public void testValid(@Valid C c){
        System.out.println("Это c  "+ c);
    }
}

@Getter
@Setter
class C {
    @Range
    private LocalDateTime localDateTime;

    @Range(min=0, max=10)
    private int testInt;
}
