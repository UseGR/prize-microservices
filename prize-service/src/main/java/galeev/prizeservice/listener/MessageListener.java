package galeev.prizeservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.prizeservice.message.InputFromAuthServiceMessage;
import galeev.prizeservice.service.Processor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ObjectMapper objectMapper;
    private final Processor processor;
    @SneakyThrows
    @KafkaListener(topics = "output-prize-service-message-topic", groupId = "prize_group_id")
    public void messageHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputFromAuthServiceMessage.class))
                .doOnNext(processor::processRequest)
                .subscribe();
    }
}
