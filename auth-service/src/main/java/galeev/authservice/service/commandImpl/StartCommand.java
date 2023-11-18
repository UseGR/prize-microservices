package galeev.authservice.service.commandImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Command;
import galeev.authservice.service.UserService;
import galeev.authservice.util.UserFieldChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final UserService userService;
    private final UserFieldChecker userFieldChecker;

    @Override
    public Flux<SendMessage> handleCommand(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
                    return userService.findById(telegramUser.getId())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(optionalUser -> {
                                if (optionalUser.isEmpty()) {
                                    User user = new User();
                                    user.setId(update.getMessage().getChatId());
                                    user.setFirstname(update.getMessage().getFrom().getFirstName());

                                    String username = update.getMessage().getFrom().getUserName() == null ? "no_username"
                                    : update.getMessage().getFrom().getUserName();

                                    user.setUsername(username);

                                    return userService.save(user)
                                            .flatMap(savedUser -> {
                                                InlineKeyboardMarkup markup = userFieldChecker.enrichUser(savedUser);
                                                return Mono.just(SendMessage.builder()
                                                        .chatId(update.getMessage().getChatId())
                                                        .text("Зарегистрируйтесь, пожалуйста")
                                                        .replyMarkup(markup)
                                                        .build());
                                            });
                                } else {
                                    return Mono.just(SendMessage.builder()
                                            .chatId(update.getMessage().getChatId())
                                            .text("Привет, " + optionalUser.orElseThrow().getFirstname())
                                            .build());
                                }
                            });
                });

    }

    @Override
    public String getType() {
        return "/start";
    }
}
