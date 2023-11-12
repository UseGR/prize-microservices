package galeev.authservice.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public record InputMessage(Long id, Update update, MessageType type) {
    public enum MessageType {
        MESSAGE, CALLBACK
    }
}
