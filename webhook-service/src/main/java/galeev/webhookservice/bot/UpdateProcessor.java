package galeev.webhookservice.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.message.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private TelegramBot telegramBot;

    @SneakyThrows
    public void processInputUpdate(Update update) {
        Mono.just(update)
                .subscribe(result -> {
                    if (result.hasMessage()) {
                        OutputMessage outputMessage = new OutputMessage(update.getMessage().getChatId(), result, OutputMessage.MessageType.MESSAGE);
                        try {
                            kafkaTemplate.send("output-message-topic", objectMapper.writeValueAsString(outputMessage));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (result.hasCallbackQuery()) {
                        if (result.getCallbackQuery().getData().startsWith("@phone")
                                || result.getCallbackQuery().getData().startsWith("@fio")
                                || result.getCallbackQuery().getData().startsWith("@dateOfBirth")
                                || result.getCallbackQuery().getData().startsWith("day@")
                                || result.getCallbackQuery().getData().startsWith("month@")
                                || result.getCallbackQuery().getData().startsWith("@sex")
                                || result.getCallbackQuery().getData().startsWith("sex@")
                                || result.getCallbackQuery().getData().startsWith("knowFrom@")
                                || result.getCallbackQuery().getData().startsWith("@knowFrom")
                        ) {
                            telegramBot.deleteMessage(result.getCallbackQuery().getFrom().getId(), result.getCallbackQuery().getMessage().getMessageId());
                        }
                        OutputMessage callback = new OutputMessage(update.getCallbackQuery().getFrom().getId(), result, OutputMessage.MessageType.CALLBACK);
                        try {
                            kafkaTemplate.send("output-callback-topic", objectMapper.writeValueAsString(callback));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}
