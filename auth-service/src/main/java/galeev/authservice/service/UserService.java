package galeev.authservice.service;

import galeev.authservice.entity.User;
import galeev.authservice.repository.UserRepository;
import galeev.authservice.util.UserFieldChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private UserFieldChecker userFieldChecker;

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<SendMessage> updateUserAndCheckEmptyFields(User user, Update update, String messageToSend) {
        return save(user)
                .flatMap(savedUser -> {
                    Mono<List<Map<String, String>>> fields = userFieldChecker.checkUserFields(update);

                    return fields.flatMap(fieldsList -> {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(user.getId());

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

                        sendMessage.setText(messageToSend);

                        return Mono.just(sendMessage);
                    });
                });
    }

    public void registerUserFieldChecker(UserFieldChecker userFieldChecker) {
        this.userFieldChecker = userFieldChecker;
    }
}
