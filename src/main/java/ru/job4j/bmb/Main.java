package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.services.TelegramBotService;
import java.util.ArrayList;

@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner initialize(MoodRepository moodRepository,
                                 MoodContentRepository moodContentRepository) {
        return args -> {
            var moods = moodRepository.findAll();
            if (!moods.isEmpty()) {
                return;
            }
            var data = new ArrayList<MoodContent>();
            data.add(new MoodContent(
                    new Mood("Потерял носок \uD83D\uDE22"),
                    "Носки — это коварные создания. Но не волнуйся, второй обязательно найдётся!"));
            data.add(new MoodContent(new Mood("Как огурец на полке \uD83D\uDE10"),
                    "Огурец тоже дело серьёзное! Главное, не мариноваться слишком долго."));
            data.add(new MoodContent(new Mood("Готов к танцам \uD83D\uDE04"),
                    "Супер! Танцуй, как будто никто не смотрит. Или, наоборот, как будто все смотрят!"));
            data.add(new MoodContent(new Mood("Где мой кофе?! \uD83D\uDE23"),
                    "Кофе уже в пути! Осталось только подождать... И ещё немного подождать..."));
            data.add(new MoodContent(new Mood("Слипаются глаза \uD83D\uDE29"),
                    "Пора на боковую! Даже супергерои отдыхают, ты не исключение."));
            for (var content : data) {
                var saved = moodRepository.save(content.getMood());
                content.setMood(saved);
                moodContentRepository.save(content);
            }
        };
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramBotService.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
                System.out.println("Бот успешно зарегистрирован");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }
}
