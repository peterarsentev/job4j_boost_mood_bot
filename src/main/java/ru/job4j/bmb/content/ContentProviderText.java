package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import ru.job4j.bmb.repository.MoodContentRepository;

@Component
public class ContentProviderText implements ContentProvider {
    private final MoodContentRepository moodContentRepository;

    public ContentProviderText(MoodContentRepository moodContentRepository) {
        this.moodContentRepository = moodContentRepository;
    }

    @Override
    public Content byMood(Long chatId, Long moodId) {
        var content = new Content(chatId);
        content.setText(moodContentRepository.findByMoodId(moodId)
                        .iterator().next().getText());
        return content;
    }
}
