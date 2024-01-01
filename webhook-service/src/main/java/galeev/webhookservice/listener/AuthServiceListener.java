package galeev.webhookservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.bot.TelegramBot;
import galeev.webhookservice.message.InputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

                            if (inputMessage.documentMessage() != null) {
                                String chatId = inputMessage.documentMessage().getChatId();
                                try {
                                    File document = ResourceUtils.getFile("Зарегистрированные.xlsx");
                                    SendDocument sendDocument = SendDocument.builder()
                                            .chatId(chatId)
                                            .caption("Таблица зарегистрировавшихся")
                                            .document(new InputFile(new FileInputStream(document), "Зарегистрированные.xlsx"))
                                            .build();
                                    telegramBot.sendMedia(sendDocument);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                );
    }

    @SneakyThrows
    @KafkaListener(topics = "input-callback-topic", groupId = "webhook_group_id")
    public void callbackHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputMessage.class))
                .subscribe(inputMessage -> {
                            if (inputMessage.simpleMessage() != null) {
                                telegramBot.sendMessage(inputMessage.simpleMessage());
                            }

                            if (inputMessage.documentMessage() != null) {
                                telegramBot.sendMedia(inputMessage.documentMessage());
                            }
                        }
                );
    }
}
