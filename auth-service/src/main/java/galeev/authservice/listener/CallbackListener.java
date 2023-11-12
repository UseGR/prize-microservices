package galeev.authservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackListener {
    private final ObjectMapper objectMapper;
    @SneakyThrows
    @KafkaListener(topics = "output-callback-topic", groupId = "group-id")
    public void messageHandler(String message) {
        InputMessage inputMessageObject = objectMapper.readValue(message, InputMessage.class);
        log.info("{}", inputMessageObject);
    }
}
