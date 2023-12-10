package galeev.authservice.service.callbackImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Callback;
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
public class SaveKnowFromCallback implements Callback {
    private final UserService userService;

    @Override
    public Flux<BotApiMethodMessage> handleCallback(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getCallbackQuery().getFrom();
                    return userService.findById(telegramUser.getId())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(optionalUser -> {
                                if (optionalUser.isPresent()) {
                                    User user = optionalUser.get();

                                    String source = update.getCallbackQuery().getData().replace("knowFrom@", "").trim();
                                    if (source.equals(User.KnowFrom.INSTAGRAM.name())) {
                                        user.setKnowFrom(User.KnowFrom.INSTAGRAM);
                                    }

                                    if (source.equals(User.KnowFrom.TELEGRAM.name())) {
                                        user.setKnowFrom(User.KnowFrom.TELEGRAM);
                                    }

                                    if (source.equals(User.KnowFrom.WHATSAPP.name())) {
                                        user.setKnowFrom(User.KnowFrom.WHATSAPP);
                                    }

                                    return userService.updateUserAndCheckEmptyFields(user, update, "Ваш выбор сохранен");
                                }
                                log.error("user with id = {} wasn't found", telegramUser.getId());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public String getType() {
        return "knowFrom@";
    }
}
