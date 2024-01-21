package galeev.webhookservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public record InputFromPrizeServiceMessage(SendMessage simpleMessage, SendPhoto photoMessage, SendAnimation animationMessage) {
}
