package galeev.webhookservice.bot;

import galeev.webhookservice.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final KafkaTemplate<String, Message> kafkaTemplate;
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processInputUpdate(Update update) {
        if (update.hasMessage()) {
            log.info("{}", update);
            kafkaTemplate.send("output-message-topic", new Message(update.getMessage().getChatId(), update, Message.MessageType.MESSAGE));
        }

        if (update.hasCallbackQuery()) {
            update.getCallbackQuery().getFrom().getId();
        }
    }
}
