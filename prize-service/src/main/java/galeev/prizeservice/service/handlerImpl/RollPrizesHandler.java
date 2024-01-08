package galeev.prizeservice.service.handlerImpl;

import galeev.prizeservice.mapper.PrizeMapper;
import galeev.prizeservice.service.Handler;
import galeev.prizeservice.service.PrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RollPrizesHandler implements Handler {
    private final PrizeService prizeService;
    @Value(value = "${application.admin.id}")
    private String adminId;
    private final PrizeMapper prizeMapper;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {
        return prizeService.findAll()
                .filter(prize -> !prize.isRolled() && prize.getUserId() == null)
                .collectList()
                .flatMapMany(prizes -> {
                    if (!prizes.isEmpty()) {
                        return Flux.fromIterable(prizes)
                                .flatMap(prize -> Mono.just(SendMessage.builder()
                                        .chatId(adminId)
                                        .text(prize.getPrizeDescription())
                                        .replyMarkup(InlineKeyboardMarkup.builder()
                                                .keyboardRow(List.of(InlineKeyboardButton.builder()
                                                        .text("Разыграть")
                                                        .callbackData("rollPrize@" + prize.getId())
                                                        .build()))
                                                .build())
                                        .build()));
                    } else {
                        return Flux.just(SendMessage.builder()
                                .chatId(adminId)
                                .text("Неразыгранные лоты отсутствуют")
                                .build());
                    }
                });
    }

    @Override
    public String getType() {
        return "Разыграть призы";
    }
}
