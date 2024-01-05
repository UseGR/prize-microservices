package galeev.prizeservice.service.handlerImpl;

import galeev.prizeservice.service.Handler;
import galeev.prizeservice.service.PrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageHandler implements Handler {
    private final PrizeService prizeService;
    @Value(value = "${application.admin.id}")
    private String adminId;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {
        return Flux.just(update)
                .flatMap(update1 -> {
                    List<PhotoSize> photos = update.getMessage().getPhoto();
                    return prizeService.findPrizeWithoutFileId()
                            .flatMap(prize -> {
                                prize.setFileId(photos.get(photos.size() - 1).getFileId());
                                return prizeService.save(prize)
                                        .flatMap(savedPrize -> Mono.just(SendMessage.builder()
                                                .chatId(adminId)
                                                .text("Изображение сохранено")
                                                .replyMarkup(InlineKeyboardMarkup.builder()
                                                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                                .text("Создать еще один лот")
                                                                .webApp(WebAppInfo.builder()
                                                                        .url("https://usegr.github.io/prize-microservices-front/#/")
                                                                        .build())
                                                                .build()))
                                                        .build())
                                                .build()));
                            })
                            .doOnError(error -> {
                                prizeService.deleteByFileIdNull().subscribe();
                            })
                            .onErrorReturn(SendMessage.builder()
                                    .chatId(adminId)
                                    .text("Нельзя начать создавать новый лот, не закончив старый. Проверьте список лотов")
                                    .build());
                });
    }

    @Override
    public String getType() {
        return "img";
    }
}
