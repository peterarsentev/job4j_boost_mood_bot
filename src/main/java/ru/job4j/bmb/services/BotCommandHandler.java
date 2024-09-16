package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.job4j.bmb.component.TgUI;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodRepository moodRepository;
    private final MoodService moodService;
    private final TgUI tgUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodRepository moodRepository,
                             MoodService moodService, TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodRepository = moodRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    Optional<Content> receive(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            if ("/start".equals(message.getText())) {
                long chatId = message.getChatId();
                var user = new User();
                user.setClientId(message.getFrom().getId());
                user.setChatId(chatId);
                userRepository.save(user);
                var content = new Content(user.getChatId());
                content.setText("Как настроение?");
                content.setMarkup(tgUI.buildButtons());
                return Optional.of(content);
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
}
