package com.example.telegram_bot_rabbitmq.controller;

import com.example.telegram_bot_rabbitmq.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TgBotController {
    private final TelegramBot telegramBot;

    @PostMapping("/rabbit")
    public ResponseEntity<String> login(@RequestParam("message") String message) {
        return new ResponseEntity<>(telegramBot.send(message), HttpStatus.OK);
    }
    @RabbitListener(queues = "new_telegram")
    public void receiveMessage(String message) {
        telegramBot.sendMessage(message);
        System.out.println(message);
    }
}

