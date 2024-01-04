package galeev.authservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputFromWebhookServiceMessage;
import galeev.authservice.service.processorImpl.CommandProcessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ObjectMapper objectMapper;
    private final CommandProcessor processor;
    @SneakyThrows
    @KafkaListener(topics = "output-message-topic", groupId = "auth-group-id")
    public void messageHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputFromWebhookServiceMessage.class))
                .map(Optional::of)
                .doOnNext(optionalMessage -> optionalMessage
                        .ifPresentOrElse(processor::processRequest,
                                () -> log.error("empty message")))
                .subscribe();
    }
}
