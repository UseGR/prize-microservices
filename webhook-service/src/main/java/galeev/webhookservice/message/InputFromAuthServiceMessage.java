package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public record InputFromAuthServiceMessage(SendMessage simpleMessage, SendDocument documentMessage) {
}
