package com.smirnov.carwashspring.controlleremail;


import com.smirnov.carwashspring.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {


    private final EmailService emailService;


    /**
     * Отправляет сообщение для подтверждения записи по ее идентификатору.
     * @param email Электронная почта
     * @param id Идентификатор записи
     */
    @GetMapping(value = "/{user-email}/{id}")
    public void sendSimpleEmail(@PathVariable("user-email") String email, @PathVariable("id") Integer id) {
        emailService.sendSimpleEmail(email, "Подтверждение записи",
                ("Для подтверждения записи перейдите по ссылке\n" +
                        "http://localhost:8080/recordings/%d/approve").formatted(id));
    }

}
