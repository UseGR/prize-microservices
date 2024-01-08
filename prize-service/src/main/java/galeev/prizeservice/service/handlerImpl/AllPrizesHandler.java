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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
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
                        SendAnimation sendAnimation = new SendAnimation();
                        sendAnimation.setChatId(adminId);
                        sendAnimation.setCaption(prizeDataDto.toString());
                        sendAnimation.setAnimation(new InputFile(prize.getFileId()));

                        return Mono.just(sendAnimation);
                    } else {
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(adminId);
                        sendPhoto.setCaption(prizeDataDto.toString());
                        sendPhoto.setPhoto(new InputFile(prize.getFileId()));

                        return Mono.just(sendPhoto);
                    }
                });
    }

    @Override
    public String getType() {
        return "Посмотреть список лотов для розыгрыша";
    }
}
