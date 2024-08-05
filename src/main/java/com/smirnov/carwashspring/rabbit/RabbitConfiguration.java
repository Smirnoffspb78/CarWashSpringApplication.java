package com.smirnov.carwashspring.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {

    @Value("${rabbitconfigure.username}")
    private String username;

    @Value("${rabbitconfigure.password}")
    private String password;

    @Value("${rabbitconfigure.virtualhost}")
    private String virtualHost;

    @Value("${rabbitconfigure.queue}")
    private String queue;

    @Value("${rabbitconfigure.exchange}")
    private String exchange;

    @Value("${rabbitconfigure.routingKey}")
    private String routingKey;

    @Value("${rabbitconfigure.host}")
    private String host;



    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setVirtualHost(virtualHost);
        return cachingConnectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
    @Bean
    public Queue myQueue() {
        return new Queue(queue);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }
}