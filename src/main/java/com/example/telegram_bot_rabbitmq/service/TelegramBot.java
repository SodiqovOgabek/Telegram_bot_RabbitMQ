package com.example.telegram_bot_rabbitmq.service;



import com.example.telegram_bot_rabbitmq.config.BotConfig;
import com.example.telegram_bot_rabbitmq.config.RabbitMQConfig;
import com.example.telegram_bot_rabbitmq.model.User;
import com.example.telegram_bot_rabbitmq.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;



    public TelegramBot(BotConfig config) {
        this.config = config;
//        List<BotCommand> listofCommands = new ArrayList<>();
//        listofCommands.add(new BotCommand("/start", "get a welcome message"));
//        try {
//            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
//        } catch (TelegramApiException e) {
//            log.error("Error setting bot's command list: " + e.getMessage());
//        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        registerUser(update.getMessage());
//        System.out.println(update.getMessage());
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//                if (messageText.equals("/start")) {
//                    System.out.println(update.getMessage());
//                    registerUser(update.getMessage());
//                }
//            }
    }

    private void registerUser(Message msg) {

        if (userRepository.findByChatId(msg.getChatId()).isEmpty()) {

            User user = User.builder()
                    .chatId(msg.getChatId())
                    .firstName(msg.getChat().getFirstName())
                    .lastName(msg.getChat().getLastName())
                    .userName(msg.getChat().getUserName())
                    .status(msg.getChat().getType())
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }


    public String sendMessage(String msg) {
        SendMessage message = new SendMessage();
        Optional<List<User>> byAll = userRepository.findByAll();
        for (User user : byAll.get()) {
            message.setChatId(String.valueOf(user.getChatId()));
            message.setText(msg);
            executeMessage(message);
        }
        return msg;
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String send(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY, message);
        return message;
    }


    public  void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

}
