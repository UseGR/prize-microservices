package galeev.webhookservice.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processInputUpdate(Update update) {
        if (update.hasMessage()) {
            log.info("{}", update);
        }

        if (update.hasCallbackQuery()) {
            update.getCallbackQuery().getFrom().getId();
        }
    }
}
