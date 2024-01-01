package galeev.authservice.service.commandImpl;

import galeev.authservice.service.Command;
import galeev.authservice.service.ExcelService;
import galeev.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelCommand implements Command {
    private final UserService userService;
    private final ExcelService excelService;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
                    return userService.findById(telegramUser.getId());
                })
                .flatMap(user -> {
                    if (user.getIsAdmin()) {
                        return excelService.getRegisteredUsersFile(userService.findAll())
                                .flatMap(workbook -> Mono.fromCallable(() -> {
                                    File currDir = new File(".");
                                    String path = currDir.getAbsolutePath();
                                    String fileLocation = path.substring(0, path.length() - 1) + "Зарегистрированные.xlsx";

                                    try (FileOutputStream outputStream = new FileOutputStream(fileLocation);) {
                                        workbook.write(outputStream);
                                    }

                                    return Mono.just("Зарегистрированные.xlsx");
                                }))
                                .flatMap(fileName -> Mono.just(SendDocument.builder()
                                        .chatId(user.getId())
                                        .caption("Таблица зарегистрировавшихся")
                                        .document(new InputFile(new File("Зарегистрированные.xlsx")))
                                        .build()));

                    }
                    log.error("user with id = {}, isn't admin, try to get ExcelCommand class response",
                            user.getId());
                    return Mono.empty();
                });
    }

    @Override
    public String getType() {
        return "Выгрузить данные в таблицу Excel";
    }
}
