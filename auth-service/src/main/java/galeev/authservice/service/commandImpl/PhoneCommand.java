package galeev.authservice.service.commandImpl;

import galeev.authservice.entity.User;
import galeev.authservice.service.Command;
import galeev.authservice.service.UserService;
import galeev.authservice.util.UserFieldChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneCommand implements Command {
    private final UserService userService;
    private final UserFieldChecker userFieldChecker;

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
                                    return userService.save(user)
                                            .flatMap(savedUser -> {
                                                Mono<List<Map<String, String>>> fields = userFieldChecker.checkUserFields(update);

                                                return fields.flatMap(fieldsList -> {
                                                    SendMessage sendMessage = new SendMessage();
                                                    sendMessage.setChatId(telegramUser.getId());

                                                    if (!fieldsList.isEmpty()) {
                                                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                                                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

                                                        fieldsList.forEach(field -> {
                                                            List<InlineKeyboardButton> keyboard = new ArrayList<>();
                                                            keyboard.add(InlineKeyboardButton.builder()
                                                                    .text(field.get("name"))
                                                                    .callbackData(field.get("callbackData"))
                                                                    .build());

                                                            buttons.add(keyboard);
                                                        });

                                                        markup.setKeyboard(buttons);
                                                        sendMessage.setReplyMarkup(markup);
                                                    }

                                                    sendMessage.setText("Номер телефона сохранен");

                                                    return Mono.just(sendMessage);
                                                });
                                            });
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
