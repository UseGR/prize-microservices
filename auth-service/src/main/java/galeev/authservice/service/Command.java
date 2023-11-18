package galeev.authservice.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

public interface Command {
    Flux<SendMessage> handleCommand(Update update);

    String getType();
}
