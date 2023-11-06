package com.example.telegram_bot_rabbitmq.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User>findByChatId(Long chatId);

    @Query("select u from usersDataTable u where u.status = 'supergroup'")
    Optional<List<User>> findByAll();
}
