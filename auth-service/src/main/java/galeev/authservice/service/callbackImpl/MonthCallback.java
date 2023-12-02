package galeev.authservice.service.callbackImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Callback;
import galeev.authservice.service.DateOfBirthService;
import galeev.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthCallback implements Callback {
    private final UserService userService;
    private final DateOfBirthService dobService;

    @Override
    public Flux<? extends BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getCallbackQuery().getFrom();
                    return userService.findById(telegramUser.getId())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(optionalUser -> {
                                if (optionalUser.isPresent()) {
                                    User user = optionalUser.get();
                                    String fullDateOfBirth = dobService.getFullDateOfBirth(user.getId(), update.getCallbackQuery().getData());
                                    user.setDateOfBirth(fullDateOfBirth);

                                    return userService.updateUserAndCheckEmptyFields(user, update, "Дата рождения сохранена");
                                }

                                log.error("user with id = {} wasn't found", telegramUser.getId());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public String getType() {
        return "month@";
    }
}
