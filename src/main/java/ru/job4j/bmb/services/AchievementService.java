package ru.job4j.bmb.services;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.event.UserEvent;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    private final MoodLogRepository moodLogRepository;
    private final AwardRepository awardRepository;
    private final SentContent sentContent;
    private final AchievementRepository achievementRepository;

    public AchievementService(MoodLogRepository moodLogRepository,
                              AwardRepository awardRepository,
                              SentContent sentContent,
                              AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
        this.sentContent = sentContent;
        this.achievementRepository = achievementRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        try (var log = moodLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId())) {
            long days = log.takeWhile(moodLog -> moodLog.getMood().isGood()).count();
            awardRepository.findFirstByDaysLessThanEqualOrderByDaysDesc(days)
                    .map(award -> new Achievement(System.currentTimeMillis(), user, award))
                    .ifPresent(achievement -> {
                        achievementRepository.save(achievement);
                        var content = new Content(user.getChatId());
                        content.setText(achievement.getAward().getTitle() + "\r\n" + achievement.getAward().getDescription());
                        sentContent.sent(content);
                    });
        }
    }
}
