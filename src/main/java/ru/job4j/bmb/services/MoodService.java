package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.event.UserEvent;
import ru.job4j.bmb.model.Mood;
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

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final ApplicationEventPublisher publisher;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       ApplicationEventPublisher publisher) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.publisher = publisher;
    }

    public Content choseMood(User user, Long moodId) {
        var log = new MoodLog();
        var mood = new Mood();
        mood.setId(moodId);
        log.setMood(mood);
        log.setUser(user);
        log.setCreatedAt(System.currentTimeMillis());
        publisher.publishEvent(new UserEvent(this, user));
        moodLogRepository.save(log);
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        var oneWeekAgo = LocalDate.now()
                .minusWeeks(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        var logs = moodLogRepository.findMoodLogsForWeek(user.get().getId(), oneWeekAgo);
        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Last Week Mood Log"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        var oneMonthAgo = LocalDate.now()
                .minusMonths(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        var logs = moodLogRepository.findMoodLogsForMonth(user.get().getId(), oneMonthAgo);
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

    public Optional<Content> awards(long chatId, Long clientId) {
        var user = userRepository.findByClientId(clientId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        var achievements = achievementRepository.findByUserId(user.get().getId());
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
}
