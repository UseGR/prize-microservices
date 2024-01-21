package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public record OutputToAuthServiceMessage(Long id, Update update, MessageType type) {
    public enum MessageType {
        MESSAGE, CALLBACK
    }
}
