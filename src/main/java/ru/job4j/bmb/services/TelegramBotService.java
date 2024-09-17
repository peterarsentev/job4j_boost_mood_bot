package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.expection.SentContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        try {
            if (content.getAudio() != null) {
                var message = new SendAudio(content.getChatId().toString(), content.getAudio());
                message.setCaption(content.getText());
                execute(message);
            } else if (content.getText() != null && content.getMarkup() != null) {
              var markup = new SendMessage(content.getChatId().toString(), content.getText());
              markup.setReplyMarkup(content.getMarkup());
              execute(markup);
            } else if (content.getText() != null) {
                execute(new SendMessage(content.getChatId().toString(), content.getText()));
            } else if (content.getPhoto() != null) {
                var message = new SendPhoto(content.getChatId().toString(), content.getPhoto());
                message.setCaption(content.getText());
                execute(message);
            }
        } catch (TelegramApiException e) {
            throw new SentContentException("Can't sent a content : " + content, e);
        }
    }
}
