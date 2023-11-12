package galeev.webhookservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.bot.TelegramBot;
import galeev.webhookservice.message.InputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceListener {
    private final ObjectMapper objectMapper;
    private final TelegramBot telegramBot;

    @SneakyThrows
    @KafkaListener(topics = "input-message-topic", groupId = "webhook_group_id")
    public void messageHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputMessage.class))
                .subscribe(inputMessage -> {
                            if (inputMessage.simpleMessage() != null) {
                                telegramBot.sendMessage(inputMessage.simpleMessage());
                            }

                            if (inputMessage.mediaMessage() != null) {
                                telegramBot.sendMedia(inputMessage.mediaMessage());
                            }
                        }
                );
    }
}
