package ru.job4j.bmb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.job4j.bmb.model.Message;
import ru.job4j.bmb.services.TelegramBotService;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext("ru.job4j.bmb.services");
        var tg = context.getBean(TelegramBotService.class);
        var message = new Message(1, "Petr Arsentev", "Hello, Bot, I'm in a good mood today.");
        tg.receive(message);
        context.close();
    }
}
