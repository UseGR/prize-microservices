package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public record InputFromPrizeServiceMessage(SendMessage simpleMessage) {
}
