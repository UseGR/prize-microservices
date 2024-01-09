package galeev.prizeservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.prizeservice.dto.UserDto;
import galeev.prizeservice.entity.Prize;
import galeev.prizeservice.message.OutputToWebhookServiceMessage;
import galeev.prizeservice.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class PrizeService {
    private final PrizeRepository prizeRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Value(value = "${application.admin.id}")
    private String adminId;

    @SneakyThrows
    public Mono<Prize> createNewPrize(Prize prize) {
        kafkaTemplate.send("input-prize-service-message-topic",
                objectMapper.writeValueAsString(new OutputToWebhookServiceMessage(SendMessage.builder()
                        .chatId(adminId)
                        .text("Теперь добавь изображение или анимацию к лоту")
                        .build(), null, null)));
        return prizeRepository.save(prize);
    }

    public Mono<Prize> save(Prize prize) {
        return prizeRepository.save(prize);
    }

    public Mono<Prize> findById(UUID id) {
        return prizeRepository.findById(id);
    }

    public Flux<Prize> findAll() {
        return prizeRepository.findAll();
    }

    public Mono<Prize> findPrizeWithoutFileId() {
        return prizeRepository.findByFileIdNull();
    }

    public Flux<Void> deleteByFileIdNull() {
        return prizeRepository.deleteByFileIdNull();
    }

    public void setWinnerToPrize(UserDto winner) {
        findById(UUID.fromString(winner.prizeId()))
                .subscribe(prize -> {
                        prize.setRolled(true);
                        prize.setUserId(winner.id());

                        save(prize)
                                .doOnNext(savedPrize -> {
                                    sendResponse(savedPrize, winner.id(), savedPrize.getWinnerDescription());
                                    sendResponse(savedPrize, Long.parseLong(adminId), prepareResponseForAdmin(winner, savedPrize));
                                })
                                .subscribe();
                });
    }

    private String prepareResponseForAdmin(UserDto user, Prize prize) {
        return String.format("Есть победитель%n" +
                        "ФИО: %s%n" +
                        "Ник: @%s%n" +
                        "Номер телефона: %s%n" +
                        "Выигрыш: %s",
                user.fullname(),
                user.username(),
                user.phoneNumber(),
                prize.getPrizeDescription());
    }

    private void sendResponse(Prize savedPrize, Long userId, String description) {
        OutputToWebhookServiceMessage outputToWebhookServiceMessage =
                savedPrize.isAnimation() ?
                        new OutputToWebhookServiceMessage(null,
                                null,
                                SendAnimation.builder()
                                        .chatId(userId)
                                        .animation(new InputFile(savedPrize.getFileId()))
                                        .caption(description)
                                        .build()) :
                        new OutputToWebhookServiceMessage(null,
                                SendPhoto.builder()
                                        .chatId(userId)
                                        .photo(new InputFile(savedPrize.getFileId()))
                                        .caption(description)
                                        .build(),
                                null);

        try {
            kafkaTemplate.send("input-prize-service-message-topic",
                    objectMapper.writeValueAsString(outputToWebhookServiceMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
