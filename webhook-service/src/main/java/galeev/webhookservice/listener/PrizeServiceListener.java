package galeev.webhookservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.bot.TelegramBot;
import galeev.webhookservice.message.InputFromPrizeServiceMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrizeServiceListener {
    private final ObjectMapper objectMapper;
    private final TelegramBot telegramBot;

    @SneakyThrows
    @KafkaListener(topics = "input-prize-service-message-topic", groupId = "webhook_group_id")
    public void messageHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputFromPrizeServiceMessage.class))
                .subscribe(inputFromPrizeServiceMessage -> {
                    if (inputFromPrizeServiceMessage.simpleMessage() != null) {
                        telegramBot.sendMessage(inputFromPrizeServiceMessage.simpleMessage());
                    }

                    if (inputFromPrizeServiceMessage.photoMessage() != null) {
                        telegramBot.sendMedia(inputFromPrizeServiceMessage.photoMessage());
                    }

                    if (inputFromPrizeServiceMessage.animationMessage() != null) {
                        telegramBot.sendMedia(inputFromPrizeServiceMessage.animationMessage());
                    }
                });
    }

}
