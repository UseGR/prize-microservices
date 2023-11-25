package galeev.authservice.service.callbackImpl;

import galeev.authservice.service.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class ChooseKnowFromCallback implements Callback {
    @Override
    public Flux<? extends BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(SendMessage.builder()
                .text("Откуда Вы узнали о мероприятии?")
                .chatId(update.getCallbackQuery().getFrom().getId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(InlineKeyboardButton.builder()
                                        .text("Instagram")
                                        .callbackData("knowFrom@INSTAGRAM")
                                        .build()),
                                List.of(InlineKeyboardButton.builder()
                                        .text("Telegram")
                                        .callbackData("knowFrom@TELEGRAM")
                                        .build()),
                                List.of(InlineKeyboardButton.builder()
                                        .text("WhatsApp")
                                        .callbackData("knowFrom@WHATSAPP")
                                        .build())))
                        .build())
                .build());
    }

    @Override
    public String getType() {
        return "@knowFrom";
    }
}
