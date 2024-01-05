package galeev.prizeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.prizeservice.entity.Prize;
import galeev.prizeservice.message.OutputToWebhookServiceMessage;
import galeev.prizeservice.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
                        .build())));
        return prizeRepository.save(prize);
    }

    public Mono<Prize> save(Prize prize) {
        return prizeRepository.save(prize);
    }

    public Mono<Prize> findById(UUID id) {
        return prizeRepository.findById(id);
    }

    public Mono<Prize> findPrizeWithoutFileId() {
        return prizeRepository.findByFileIdNull();
    }

    public Flux<Void> deleteByFileIdNull() {
        return prizeRepository.deleteByFileIdNull();
    }
}
