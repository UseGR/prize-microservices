package galeev.authservice.service.commandImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Command;
import galeev.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullnameCommand implements Command {
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
                                    if (user.getFullname() == null) {
                                        String text = update.getMessage().getText();

                                        if (text.split(" ").length < 3) {
                                            log.error("user input: {}, user entity: {}", text, telegramUser);

                                            return Mono.just(SendMessage.builder()
                                                    .chatId(telegramUser.getId())
                                                    .text("Некорректный ввод. Попробуйте ввести еще раз")
                                                    .build());
                                        }

                                        user.setFullname(text);

                                        return userService.updateUserAndCheckEmptyFields(user, update, "ФИО сохранено");
                                    } else {
                                        return Mono.just(SendMessage.builder()
                                                .chatId(telegramUser.getId())
                                                .text("Некорректный ввод. Попробуйте ввести еще раз")
                                                .build());
                                    }
                                }

                                log.error("user with id = {} wasn't found", telegramUser.getId());
                                return Mono.empty();
                            });
                });
    }

    @Override
    public String getType() {
        return "full_name";
    }
}
