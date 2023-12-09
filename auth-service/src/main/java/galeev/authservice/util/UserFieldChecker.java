package galeev.authservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.entity.User;
import galeev.authservice.message.OutputMessage;
import galeev.authservice.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserFieldChecker {
    private final UserService userService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InlineKeyboardMarkup enrichUser(User user) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if (user.getPhoneNumber() == null) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton phoneNumberButton = new InlineKeyboardButton();
            phoneNumberButton.setText("Номер телефона");
            phoneNumberButton.setCallbackData("@phone");
            button.add(phoneNumberButton);
            keyboard.add(button);
        }

        if (user.getDateOfBirth() == null) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton dateOfBirthButton = new InlineKeyboardButton();
            dateOfBirthButton.setText("Дата рождения");
            dateOfBirthButton.setCallbackData("@dateOfBirth");
            button.add(dateOfBirthButton);
            keyboard.add(button);
        }

        if (user.getFullname() == null) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton lastnameButton = new InlineKeyboardButton();
            lastnameButton.setText("ФИО");
            lastnameButton.setCallbackData("@fio");
            button.add(lastnameButton);
            keyboard.add(button);
        }


        if (user.getDateOfBirth() == null) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton sexButton = new InlineKeyboardButton();
            sexButton.setText("Пол");
            sexButton.setCallbackData("@sex");
            button.add(sexButton);
            keyboard.add(button);
        }

        if (user.getKnowFrom() == null) {
            List<InlineKeyboardButton> button = new ArrayList<>();
            InlineKeyboardButton knowFromButton = new InlineKeyboardButton();
            knowFromButton.setText("Откуда Вы узнали о мероприятии?");
            knowFromButton.setCallbackData("@knowFrom");
            button.add(knowFromButton);
            keyboard.add(button);
        }

        markup.setKeyboard(keyboard);

        return markup;
    }

    public Mono<List<Map<String, String>>> checkUserFields(Update update) {
        Mono<User> user = userService.findById(update.getCallbackQuery() != null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getChatId());

        return user.flatMap(subscribedUser -> {
            List<Map<String, String>> fields = new ArrayList<>();
            if (subscribedUser.getPhoneNumber() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "Номер телефона");
                map.put("callbackData", "@phone");
                fields.add(map);
            }

            if (subscribedUser.getDateOfBirth() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "Дата рождения");
                map.put("callbackData", "@dateOfBirth");
                fields.add(map);
            }

            if (subscribedUser.getFullname() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "ФИО");
                map.put("callbackData", "@fio");
                fields.add(map);
            }

            if (subscribedUser.getUsername() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "Ник");
                map.put("callbackData", "@username");
                fields.add(map);
            }


            if (subscribedUser.getSex() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "Пол");
                map.put("callbackData", "@sex");
                fields.add(map);
            }

            if (subscribedUser.getKnowFrom() == null) {
                Map<String, String> map = new HashMap<>();
                map.put("name", "Откуда Вы узнали о мероприятии?");
                map.put("callbackData", "@knowFrom");
                fields.add(map);
            }

            if (!fields.isEmpty()) {
                return Mono.just(fields);
            }

            return Mono.just(Collections.emptyList());
        });
    }

    @PostConstruct
    public void init() {
        userService.registerUserFieldChecker(this);
    }

    public void isRegistrationComplete(Update update) {
        Long id = update.hasMessage() ? update.getMessage().getFrom().getId() : update.getCallbackQuery().getFrom().getId();
        checkUserFields(update)
                .subscribe(fields -> {
                    if (fields.isEmpty()) {
                        userService.findById(id)
                                .subscribe(user -> {
                                    SendMessage sendMessage = new SendMessage();
                                    sendMessage.setChatId(id);
                                    sendMessage.setText("Спасибо, регистрация завершена");

                                    if (user.isAdmin()) {
                                        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                                        keyboardMarkup.setResizeKeyboard(true);
                                        keyboardMarkup.setKeyboard(List.of(
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Сформировать лот для розыгрыша")
                                                        .build())),
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Посмотреть всех пользователей")
                                                        .build())),
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Посмотреть список лотов для розыгрыша")
                                                        .build())),
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Разыграть призы")
                                                        .build())),
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Выгрузить данные в таблицу Excel")
                                                        .build())),
                                                new KeyboardRow(List.of(KeyboardButton.builder()
                                                        .text("Посмотреть черный список")
                                                        .build()))
                                        ));
                                        sendMessage.setReplyMarkup(keyboardMarkup);
                                    }

                                    try {
                                        OutputMessage outputMessage = new OutputMessage(sendMessage, null);
                                        kafkaTemplate.send("input-message-topic", objectMapper.writeValueAsString(outputMessage));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    }
                });
    }
}
