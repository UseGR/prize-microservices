package galeev.prizeservice.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public record InputFromAuthServiceMessage(Long id, Update update, MessageType type) {
    public enum MessageType {
        MESSAGE, CALLBACK
    }
}
