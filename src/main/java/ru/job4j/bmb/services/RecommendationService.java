package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class RecommendationService {
    private final SentContent sentContent;
    private final UserRepository userRepository;

    public RecommendationService(SentContent sentContent, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.sentContent = sentContent;
    }

//    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void ping() {
        for (var user : userRepository.findAll()) {
            var content = new Content(user.getChatId());
            content.setText("Ping");
            sentContent.sent(content);
        }
    }
}
