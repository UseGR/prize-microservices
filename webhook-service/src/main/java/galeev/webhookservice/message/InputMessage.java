package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public record InputMessage(SendMessage simpleMessage, SendMediaBotMethod<Message> mediaMessage) {
}
