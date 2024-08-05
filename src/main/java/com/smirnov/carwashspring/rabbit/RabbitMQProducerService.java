package com.smirnov.carwashspring.rabbit;

import com.smirnov.carwashspring.dto.request.create.MessageRecording;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitconfigure.exchange}")
    private String exchange;

    @Value("${rabbitconfigure.routingKey}")
    private String routingKey;

    public void sendMessage(Integer id, String email) {
        MessageRecording messageRecording = new MessageRecording(id, email);
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRecording);
    }
}