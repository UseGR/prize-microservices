package galeev.authservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputMessage;
import galeev.authservice.service.processorImpl.CallbackProcessor;
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
public class CallbackListener {
    private final ObjectMapper objectMapper;
    private final CallbackProcessor processor;

    @SneakyThrows
    @KafkaListener(topics = "output-callback-topic", groupId = "auth-group-id")
    public void messageHandler(String message) {
        Mono.just(objectMapper.readValue(message, InputMessage.class))
                .map(Optional::of)
                .doOnNext(optionalMessage -> optionalMessage.ifPresentOrElse(processor::processRequest,
                        () -> log.error("empty callback")))
                .subscribe();
    }
}
