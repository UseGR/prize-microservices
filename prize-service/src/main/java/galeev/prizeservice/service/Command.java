package galeev.prizeservice.service;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.io.Serializable;

public interface Command {
    Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update);

    String getType();
}
