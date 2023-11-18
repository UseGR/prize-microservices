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
public class ChooseGenderCallback implements Callback {

    @Override
    public Flux<BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(SendMessage.builder()
                .text("Выберите пол")
                .chatId(update.getCallbackQuery().getFrom().getId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                        .text("Мужской")
                                        .callbackData("sex@male")
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("Женский")
                                        .callbackData("sex@female")
                                        .build()))
                        .build())
                .build());
    }

    @Override
    public String getType() {
        return "@sex";
    }
}
