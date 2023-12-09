package galeev.authservice.message;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public record OutputMessage(BotApiMethodMessage simpleMessage, SendMediaBotMethod<Message> mediaMessage) {
}
