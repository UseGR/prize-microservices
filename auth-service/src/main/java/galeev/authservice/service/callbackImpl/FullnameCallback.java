package galeev.authservice.service.callbackImpl;

import galeev.authservice.service.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
@Component
public class FullnameCallback implements Callback {
    @Override
    public Flux<? extends BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(SendMessage.builder()
                .chatId(update.getCallbackQuery().getFrom().getId())
                .text("Введите ФИО. Пример - Иванов Иван Иванович")
                .build());
    }

    @Override
    public String getType() {
        return "@fio";
    }
}
