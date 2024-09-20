package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;
import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    Optional<Content> commands(Message message) {
        var text = message.getText();
        if ("/start".equals(text)) {
            return handleStartCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/week_mood_log".equals(text)) {
            return moodService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/month_mood_log".equals(text)) {
            return moodService.monthMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/award".equals(text)) {
            return moodService.awards(message.getChatId(), message.getFrom().getId());
        } else {
            return Optional.empty();
        }
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findByClientId(callback.getFrom().getId());
        return user.map(value -> moodService.choseMood(value, moodId));
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
}
