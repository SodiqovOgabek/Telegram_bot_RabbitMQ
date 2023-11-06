package com.example.telegram_bot_rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "new_telegram_exchange";
    public static final String ROUTING_KEY = "new_telegram_routing_key";
    public static final String QUEUE = "new_telegram";

    @Bean
    public Queue coderQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public MessageConverter convertor() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding bindingCoderQueue(TopicExchange exchange) {
        return BindingBuilder.bind(coderQueue()).to(exchange).with(ROUTING_KEY);
    }
}
