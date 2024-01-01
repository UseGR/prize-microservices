package galeev.authservice.service.commandImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Command;
import galeev.authservice.service.UserService;
import galeev.authservice.util.UserFieldChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final UserService userService;
    private final UserFieldChecker userFieldChecker;
    @Value("${application.admin.id}")
    private String adminId;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {
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

                                    if (update.getMessage().getChatId().equals(Long.parseLong(adminId))) {
                                        user.setIsAdmin(true);
                                    }

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
                                    return userFieldChecker.checkUserFields(update)
                                            .flatMap(fields -> {
                                                if (fields.isEmpty()) {
                                                    SendMessage sendMessage = SendMessage.builder()
                                                            .chatId(update.getMessage().getChatId())
                                                            .text("Привет, " + optionalUser.orElseThrow().getFirstname())
                                                            .build();

                                                    if (optionalUser.orElseThrow().getIsAdmin()) {
                                                        userFieldChecker.enrichAdmin(sendMessage);
                                                    }

                                                    return Mono.just(sendMessage);
                                                }

                                                SendMessage sendMessage = new SendMessage();
                                                sendMessage.setChatId(update.getMessage().getChatId());
                                                userFieldChecker.setAbsentFields(fields, sendMessage);
                                                sendMessage.setText("Пожалуйста, завершите регистрацию");

                                                return Mono.just(sendMessage);
                                            });
                                }
                            });
                });

    }

    @Override
    public String getType() {
        return "/start";
    }
}
