package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodLogRepository moodLogRepository;
    private final MoodService moodService;
    private final AchievementRepository achievementRepository;
    private final TgUI tgUI;
    private final  DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public BotCommandHandler(UserRepository userRepository,
                             MoodLogRepository moodLogRepository,
                             MoodService moodService,
                             AchievementRepository achievementRepository,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodLogRepository = moodLogRepository;
        this.moodService = moodService;
        this.achievementRepository = achievementRepository;
        this.tgUI = tgUI;
    }

    Optional<Content> receive(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            var text = message.getText();
            if ("/start".equals(text)) {
                return handleStartCommand(message.getChatId(), message.getFrom().getId());
            } else if ("/week_mood_log".equals(text)) {
                return handleWeekMoodLogCommand(message.getChatId(), message.getFrom().getId());
            } else if ("/month_mood_log".equals(text)) {
                return handleMonthMoodLogCommand(message.getChatId(), message.getFrom().getId());
            } else if ("/award".equals(text)) {
                return handleAwardCommand(message.getChatId(), message.getFrom().getId());
            }
        }
        if (update.hasCallbackQuery()) {
            var moodId = Long.valueOf(update.getCallbackQuery().getData());
            var clientId = update.getCallbackQuery().getFrom().getId();
            var user = userRepository.findByClientId(clientId);
            return Optional.of(moodService.getContent(user, moodId));
        }
        return Optional.empty();
    }

    private Optional<Content> handleAwardCommand(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        var achievements = achievementRepository.findByUserId(user.getId());
        var sb = new StringBuilder("Ваши награды:\n");
        achievements.forEach(achievement -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(achievement.getCreateAt()));
            sb.append(formattedDate)
                    .append(": ")
                    .append(achievement.getAward().getTitle()).append("\n");
        });

        var content = new Content(chatId);
        content.setText(sb.toString());
        return Optional.of(content);
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        userRepository.save(user);
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    private Optional<Content> handleWeekMoodLogCommand(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        var oneWeekAgo = LocalDate.now()
                .minusWeeks(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        var logs = moodLogRepository.findMoodLogsForWeek(user.getId(), oneWeekAgo);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Last Week Mood Log"));
        return Optional.of(content);
    }

    private Optional<Content> handleMonthMoodLogCommand(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        var oneMonthAgo = LocalDate.now()
                .minusMonths(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        var logs = moodLogRepository.findMoodLogsForMonth(user.getId(), oneMonthAgo);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Last Month Mood Log"));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }
}
