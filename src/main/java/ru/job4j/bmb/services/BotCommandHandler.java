package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.Message;

@Service
public class BotCommandHandler {
    void receive(Message message) {
        System.out.println(message);
    }
}
