package galeev.authservice.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public record InputFromWebhookServiceMessage(Long id, Update update, MessageType type) {
    public enum MessageType {
        MESSAGE, CALLBACK
    }
}
