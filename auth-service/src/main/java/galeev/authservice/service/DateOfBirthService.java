package galeev.authservice.service;

import galeev.authservice.dto.DobDto;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Getter
public class DateOfBirthService {
    private final List<DobDto> dobCache = new CopyOnWriteArrayList<>();

    public SendMessage generateDays(Long userId) {
        dobCache.removeIf(dto -> dto.id().equals(userId));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(fillRow(1, 7));
        keyboard.add(fillRow(8, 14));
        keyboard.add(fillRow(15, 21));
        keyboard.add(fillRow(22, 28));
        keyboard.add(fillRow(29, 31));

        markup.setKeyboard(keyboard);

        return SendMessage.builder()
                .text("Выберите день рождения")
                .chatId(userId)
                .replyMarkup(markup)
                .build();
    }

    private List<InlineKeyboardButton> fillRow(int from, int to) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = from; i <= to; i++) {
            row.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(i))
                    .callbackData("day@" + i)
                    .build());
        }

        return row;
    }

    public SendMessage generateMonths(Long userId) {
        String chosenDay = dobCache.stream()
                .filter(dto -> dto.id().equals(userId))
                .findAny()
                .orElseThrow()
                .data();

        String day = chosenDay.substring(chosenDay.indexOf("@") + 1);
        List<String> months = findMatchedMonths(day);
        Map<String, String> monthMap = generateMonthsMap(months);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        monthMap.forEach((k, v) -> {
            List<InlineKeyboardButton> monthKey = List.of(InlineKeyboardButton.builder()
                    .text(k)
                    .callbackData(v)
                    .build());
            keyboard.add(monthKey);
        });

        markup.setKeyboard(keyboard);
        return SendMessage.builder()
                .text("Выберите месяц рождения")
                .replyMarkup(markup)
                .chatId(userId)
                .build();
    }

    private Map<String, String> generateMonthsMap(List<String> months) {
        Map<String, String> hashMap = new LinkedHashMap<>();
        months.forEach(code -> {
            String monthName = convertToName(code.substring(code.indexOf("@") + 1));
            hashMap.put(monthName, code);
        });

        return hashMap;
    }

    private String convertToName(String code) {
        return switch (code) {
            case "jan" -> "Январь";
            case "feb" -> "Февраль";
            case "mar" -> "Март";
            case "apr" -> "Апрель";
            case "may" -> "Май";
            case "jun" -> "Июнь";
            case "jul" -> "Июль";
            case "aug" -> "Август";
            case "sep" -> "Сентябрь";
            case "oct" -> "Октябрь";
            case "nov" -> "Ноябрь";
            default -> "Декабрь";
        };
    }

    private List<String> findMatchedMonths(String days) {
        int chosenDay = Integer.parseInt(days);
        List<String> months = new ArrayList<>();

        for (Month month : Month.values()) {
            int daysInMonth = month.length(true);
            if (chosenDay <= daysInMonth) {
                String monthName = String.valueOf(month).toLowerCase();
                months.add("month@" + monthName.substring(0, 3));
            }
        }
        return months;
    }

    public String getFullDateOfBirth(Long userId, String callbackData) {
        String chosenDay = dobCache.stream()
                .filter(dto -> dto.id().equals(userId))
                .findAny()
                .orElseThrow()
                .data();

        String day = chosenDay.substring(4);

        String monthCoded = callbackData.substring(6);
        String month = convertToName(monthCoded);

        dobCache.removeIf(dto -> dto.id().equals(userId));

        return month + ", " + day;
    }
}
