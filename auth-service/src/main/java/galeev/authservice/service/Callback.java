package galeev.authservice.service;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

public interface Callback {
    Flux<? extends BotApiMethodMessage> handleCallback(Update update);
    String getType();
}
