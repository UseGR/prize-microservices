package galeev.webhookservice.bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private final UpdateProcessor updateProcessor;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/";
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
    }

    public void sendMessage(BotApiMethod<?> message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void sendMedia(SendMediaBotMethod<Message> message) {
        if (message != null) {
            try {
                if (message instanceof SendDocument) {
                    execute((SendDocument) message);
                } else if (message instanceof SendAnimation) {
                    execute((SendAnimation) message);
                } else if (message instanceof SendPhoto) {
                    execute((SendPhoto) message);
                }

            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("error during removing {}", e);
        }
    }
}
