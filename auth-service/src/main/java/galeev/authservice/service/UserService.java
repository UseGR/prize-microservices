package galeev.authservice.service;

import galeev.authservice.entity.User;
import galeev.authservice.mapper.UserMapper;
import galeev.authservice.repository.UserRepository;
import galeev.authservice.util.UserFieldChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private UserFieldChecker userFieldChecker;
    private final UserMapper userMapper;

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
                            userFieldChecker.setAbsentFields(fieldsList, sendMessage);
                        }

                        sendMessage.setText(messageToSend);

                        return Mono.just(sendMessage);
                    });
                });
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }


    public void registerUserFieldChecker(UserFieldChecker userFieldChecker) {
        this.userFieldChecker = userFieldChecker;
    }

    public Flux<? extends BotApiMethodMessage> getUsersData(Long chatId) {
        return Flux.just(SendMessage.builder()
                        .chatId(chatId)
                        .text("Список пользователей")
                        .build())
                .flatMap(sendMessage -> findAll()
                        .flatMap(user -> Mono.just(userMapper.toDto(user)))
                        .flatMap(userDto -> {
                            sendMessage.setText(userDto.toString());

                            if (!userDto.isAdmin()) {
                                sendMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                .text("Удалить участника")
                                                .callbackData("deleteUser@" + userDto.id())
                                                .build()))
                                        .build());
                            }

                            return Mono.just(sendMessage);
                        }));
    }
}
