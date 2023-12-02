package galeev.authservice.service.callbackImpl;

import galeev.authservice.service.Callback;
import galeev.authservice.service.DateOfBirthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class DayOfBirthCallback implements Callback {
    private final DateOfBirthService dobService;

    @Override
    public Flux<? extends BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(dobService.generateDays(update.getCallbackQuery().getFrom().getId()));
    }

    @Override
    public String getType() {
        return "@dateOfBirth";
    }
}
