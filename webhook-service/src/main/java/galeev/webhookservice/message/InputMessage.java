package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public record InputMessage(SendMessage simpleMessage, SendDocument documentMessage) {
}
