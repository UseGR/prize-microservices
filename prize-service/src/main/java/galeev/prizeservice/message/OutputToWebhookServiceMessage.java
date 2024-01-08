package galeev.prizeservice.message;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public record OutputToWebhookServiceMessage(SendMessage simpleMessage, SendPhoto photoMessage, SendAnimation animationMessage) {
}
