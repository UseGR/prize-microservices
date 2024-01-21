package galeev.webhookservice.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.message.OutputToAuthServiceMessage;
import galeev.webhookservice.message.OutputToPrizeServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private TelegramBot telegramBot;
    private final List<String> messagesToDelete = List.of("@phone", "@fio", "@dateOfBirth", "day@", "month@", "@sex",
            "sex@", "knowFrom@", "@knowFrom", "rollPrize@");

    @SneakyThrows
    public void processInputUpdate(Update update) {
        Mono.just(update)
                .subscribe(result -> {
                    if (result.hasMessage()) {
                        if (result.getMessage().hasText()) {
                            OutputToAuthServiceMessage outputToAuthServiceMessage =
                                    new OutputToAuthServiceMessage(update.getMessage().getChatId(),
                                            result,
                                            OutputToAuthServiceMessage.MessageType.MESSAGE);
                            try {
                                kafkaTemplate.send("output-message-topic",
                                        objectMapper.writeValueAsString(outputToAuthServiceMessage));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            OutputToPrizeServiceMessage outputToPrizeServiceMessage =
                                    new OutputToPrizeServiceMessage(update.getMessage().getChatId(),
                                            result,
                                            OutputToPrizeServiceMessage.MessageType.MEDIA);

                            try {
                                kafkaTemplate.send("output-message-topic",
                                        objectMapper.writeValueAsString(outputToPrizeServiceMessage));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    if (result.hasCallbackQuery()) {
                        messagesToDelete.stream()
                                .filter(prefix -> result.getCallbackQuery().getData().startsWith(prefix))
                                .findFirst()
                                .ifPresent(prefix -> telegramBot.deleteMessage(
                                        result.getCallbackQuery().getFrom().getId(),
                                        result.getCallbackQuery().getMessage().getMessageId())
                                );

                        OutputToAuthServiceMessage callback = new OutputToAuthServiceMessage(
                                update.getCallbackQuery().getFrom().getId(),
                                result,
                                OutputToAuthServiceMessage.MessageType.CALLBACK);
                        try {
                            kafkaTemplate.send("output-callback-topic",
                                    objectMapper.writeValueAsString(callback));
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
