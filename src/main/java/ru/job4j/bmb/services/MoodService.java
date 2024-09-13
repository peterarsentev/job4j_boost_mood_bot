package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;

    public MoodService(MoodLogRepository moodLogRepository, RecommendationEngine recommendationEngine) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
    }

    public Content getContent(User user, Long moodId) {
        var log = new MoodLog();
        var mood = new Mood();
        mood.setId(moodId);
        log.setMood(mood);
        log.setUser(user);
        log.setCreatedAt(System.currentTimeMillis());
        moodLogRepository.save(log);
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }
}
