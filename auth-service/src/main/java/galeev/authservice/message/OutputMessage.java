package galeev.authservice.message;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public record OutputMessage(SendMessage simpleMessage, SendMediaBotMethod<Message> mediaMessage) {
}
