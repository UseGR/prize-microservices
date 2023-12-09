package galeev.webhookservice.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.message.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private TelegramBot telegramBot;

    @SneakyThrows
    public void processInputUpdate(Update update) {
        if (update.hasMessage()) {
            log.info("got message, it's id: {}, from user with id: {}",
                    update.getMessage().getMessageId(),
                    update.getMessage().getFrom().getId());
            OutputMessage outputMessage = new OutputMessage(update.getMessage().getChatId(), update, OutputMessage.MessageType.MESSAGE);
            kafkaTemplate.send("output-message-topic", objectMapper.writeValueAsString(outputMessage));
        }

        if (update.hasCallbackQuery()) {
            log.info("got callback, it's id: {}, from user with id: {}",
                    update.getCallbackQuery().getMessage().getMessageId(),
                    update.getCallbackQuery().getFrom().getId());
            if (update.getCallbackQuery().getData().startsWith("@phone")
                    || update.getCallbackQuery().getData().startsWith("@fio")
                    || update.getCallbackQuery().getData().startsWith("@dateOfBirth")
                    || update.getCallbackQuery().getData().startsWith("day@")
                    || update.getCallbackQuery().getData().startsWith("month@")
                    || update.getCallbackQuery().getData().startsWith("@sex")
                    || update.getCallbackQuery().getData().startsWith("sex@")
                    || update.getCallbackQuery().getData().startsWith("knowFrom@")
                    || update.getCallbackQuery().getData().startsWith("@knowFrom")
            ) {
                telegramBot.deleteMessage(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery().getMessage().getMessageId());
            }
            OutputMessage callback = new OutputMessage(update.getCallbackQuery().getFrom().getId(), update, OutputMessage.MessageType.CALLBACK);
            kafkaTemplate.send("output-callback-topic", objectMapper.writeValueAsString(callback));
        }
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}
