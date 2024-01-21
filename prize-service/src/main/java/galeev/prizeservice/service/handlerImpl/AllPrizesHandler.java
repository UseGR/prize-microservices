package galeev.prizeservice.service.handlerImpl;

import galeev.prizeservice.dto.PrizeDataDto;
import galeev.prizeservice.mapper.PrizeMapper;
import galeev.prizeservice.service.Handler;
import galeev.prizeservice.service.PrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
public class AllPrizesHandler implements Handler {
    private final PrizeService prizeService;
    @Value(value = "${application.admin.id}")
    private String adminId;
    private final PrizeMapper prizeMapper;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {

        return prizeService.findAll()
                .flatMap(prize -> {
                    PrizeDataDto prizeDataDto = prizeMapper.mapData(prize);

                    if (prize.isAnimation()) {
                        return Mono.just(SendAnimation.builder()
                                .chatId(adminId)
                                .caption(prizeDataDto.toString())
                                .animation(new InputFile(prize.getFileId()))
                                .replyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                .text("Редактировать лот")
                                                .webApp(WebAppInfo.builder()
                                                        .url("https://usegr.github.io/prize-microservices-front/#/" +
                                                                prize.getId())
                                                        .build())
                                                .build()))
                                        .build())
                                .build());
                    } else {
                        return Mono.just(SendPhoto.builder()
                                .chatId(adminId)
                                .caption(prizeDataDto.toString())
                                .photo(new InputFile(prize.getFileId()))
                                .replyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                .text("Редактировать лот")
                                                .webApp(WebAppInfo.builder()
                                                        .url("https://usegr.github.io/prize-microservices-front/#/" +
                                                                prize.getId())
                                                        .build())
                                                .build()))
                                        .build())
                                .build());
                    }
                });
    }

    @Override
    public String getType() {
        return "Посмотреть список лотов для розыгрыша";
    }
}
