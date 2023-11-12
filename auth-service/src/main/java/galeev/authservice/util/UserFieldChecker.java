package galeev.authservice.util;

import galeev.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFieldChecker {
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
            knowFromButton.setText("Вы узнали про нас");
            knowFromButton.setCallbackData("@knowFrom");
            button.add(knowFromButton);
            keyboard.add(button);
        }

        markup.setKeyboard(keyboard);

        return markup;
    }
}
