package galeev.authservice.service.commandImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Command;
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
public class PhoneCommand implements Command {
    private final UserService userService;

    @Override
    public Flux<? extends BotApiMethodMessage> handleCommand(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
                    return userService.findById(telegramUser.getId())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(optionalUser -> {
                                if (optionalUser.isPresent()) {
                                    User user = optionalUser.get();
                                    user.setPhoneNumber(update.getMessage().getText());
                                    return userService.updateUserAndCheckEmptyFields(user, update, "Номер телефона сохранен");
                                }

                                log.error("user with id = {} wasn't found", telegramUser.getId());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public String getType() {
        return "^\\+?\\d{11}$";
    }
}
