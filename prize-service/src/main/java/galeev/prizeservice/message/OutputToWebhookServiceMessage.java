package galeev.prizeservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public record OutputToWebhookServiceMessage(SendMessage simpleMessage) {
}
