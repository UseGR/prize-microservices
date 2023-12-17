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

import java.util.Optional;
@Slf4j
@Component
@RequiredArgsConstructor
public class AllUsersCommand implements Command {
    private final UserService userService;
    @Override
    public Flux<? extends BotApiMethodMessage> handleCommand(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
                    return userService.findById(telegramUser.getId())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMapMany(optionalUser -> {
                                if (optionalUser.isPresent()) {
                                    User user = optionalUser.get();
                                    if (user.getIsAdmin()) {
                                        return userService.getUsersData(telegramUser.getId());
                                    }

                                    log.error("user with id = {}, isn't admin, try to get AllUserCommand class response",
                                            telegramUser.getId());
                                    return Flux.empty();
                                }
                                log.error("user with id = {} wasn't found", telegramUser.getId());
                                return Flux.empty();
                            });
                });

    }

    @Override
    public String getType() {
        return "Посмотреть всех пользователей";
    }
}
