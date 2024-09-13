package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class ContentProviderAudio implements ContentProvider {
    @Override
    public Content byMood(Long chatId, Long moodId) {
        var content = new Content(chatId);
        var imageFile = new File("./audio/sharlot.mp3");
        content.setText("Послушай Шарлота!");
        content.setAudio(new InputFile(imageFile));
        return content;
    }
}
