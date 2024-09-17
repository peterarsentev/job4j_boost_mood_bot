package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.expection.TelegramInitException;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.services.TelegramBotService;
import java.util.ArrayList;

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                 MoodContentRepository moodContentRepository,
                                 AwardRepository awardRepository) {
        return args -> {
            var moods = moodRepository.findAll();
            if (!moods.isEmpty()) {
                return;
            }
            var data = new ArrayList<MoodContent>();
            data.add(new MoodContent(
                    new Mood("Счастливейший на свете \uD83D\uDE0E", true),
                    "Невероятно! Вы сияете от счастья, продолжайте радоваться жизни."));

            data.add(new MoodContent(
                    new Mood("Восхитительное настроение \uD83D\uDE0D", true),
                    "Удивительно! Пусть это чувство длится весь день."));

            data.add(new MoodContent(
                    new Mood("Прекрасное настроение \uD83D\uDE0A", true),
                    "Ваше позитивное настроение заразительно! Продолжайте в том же духе."));

            data.add(new MoodContent(
                    new Mood("Отличное настроение \uD83D\uDE03", true),
                    "Прекрасно! Делитесь своей улыбкой с окружающими."));

            data.add(new MoodContent(
                    new Mood("Хорошее настроение \uD83D\uDE04", true),
                    "Замечательно! Пусть ваш день будет наполнен радостью."));

            data.add(new MoodContent(
                    new Mood("Неплохое настроение \uD83D\uDE42", true),
                    "Рад слышать, что у вас всё хорошо!"));

            data.add(new MoodContent(
                    new Mood("Нейтральное настроение \uD83D\uDE10", true),
                    "Неплохо! Может быть, попробуем сделать что-нибудь приятное сегодня?"));

            data.add(new MoodContent(
                    new Mood("Немного грустно \uD83D\uDE15", false),
                    "Иногда грусть помогает нам ценить радостные моменты."));

            data.add(new MoodContent(
                    new Mood("Плохое настроение \uD83D\uDE1F", false),
                    "Держитесь! Всё обязательно наладится."));

            data.add(new MoodContent(
                    new Mood("Очень плохое настроение \uD83D\uDE1E", false),
                    "Мне жаль, что вы чувствуете себя так. Помните, что вы не одиноки."));

            for (var content : data) {
                var saved = moodRepository.save(content.getMood());
                content.setMood(saved);
                moodContentRepository.save(content);
            }

            // Инициализация наград
            var awards = new ArrayList<Award>();
            awards.add(new Award("Смайлик дня", "За 1 день хорошего настроения. Награда: Веселый смайлик или стикер, отправленный пользователю в качестве поощрения.", 1));
            awards.add(new Award("Настроение недели", "За 7 последовательных дней хорошего или отличного настроения. Награда: Специальный значок или иконка, отображаемая в профиле пользователя в течение недели.", 7));
            awards.add(new Award("Бонусные очки", "За каждые 3 дня хорошего настроения. Награда: Очки, которые можно обменять на виртуальные предметы или функции внутри приложения.", 3));
            awards.add(new Award("Персонализированные рекомендации", "После 5 дней хорошего настроения. Награда: Подборка контента или активности на основе интересов пользователя.", 5));
            awards.add(new Award("Достижение \"Солнечный луч\"", "За 10 дней непрерывного хорошего настроения. Награда: Разблокировка новой темы оформления или фона в приложении.", 10));
            awards.add(new Award("Виртуальный подарок", "После 15 дней хорошего настроения. Награда: Возможность отправить или получить виртуальный подарок внутри приложения.", 15));
            awards.add(new Award("Титул \"Лучезарный\"", "За 20 дней хорошего или отличного настроения. Награда: Специальный титул, отображаемый рядом с именем пользователя.", 20));
            awards.add(new Award("Доступ к премиум-функциям", "После 30 дней хорошего настроения. Награда: Временный доступ к премиум-функциям или эксклюзивному контенту.", 30));
            awards.add(new Award("Участие в розыгрыше призов", "За каждую неделю хорошего настроения. Награда: Шанс выиграть призы в ежемесячных розыгрышах.", 7));
            awards.add(new Award("Эксклюзивный контент", "После 25 дней хорошего настроения. Награда: Доступ к эксклюзивным статьям, видео или мероприятиям.", 25));
            awards.add(new Award("Награда \"Настроение месяца\"", "За поддержание хорошего или отличного настроения в течение целого месяца. Награда: Специальный значок, признание в сообществе или дополнительные привилегии.", 30));
            awards.add(new Award("Физический подарок", "После 60 дней хорошего настроения. Награда: Возможность получить небольшой физический подарок, например, открытку или фирменный сувенир.", 60));
            awards.add(new Award("Коучинговая сессия", "После 45 дней хорошего настроения. Награда: Бесплатная сессия с коучем или консультантом для дальнейшего улучшения благополучия.", 45));
            awards.add(new Award("Разблокировка мини-игр", "После 14 дней хорошего настроения. Награда: Доступ к развлекательным мини-играм внутри приложения.", 14));
            awards.add(new Award("Персональное поздравление", "За значимые достижения (например, 50 дней хорошего настроения). Награда: Персонализированное сообщение от команды приложения или вдохновляющая цитата.", 50));
            awardRepository.saveAll(awards);
        };
    }

    @Bean
    public CommandLineRunner initTelegramApi(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramBotService.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                throw new TelegramInitException("Ошибка инициализации Telegram API", e);
            }
        };
    }
}
