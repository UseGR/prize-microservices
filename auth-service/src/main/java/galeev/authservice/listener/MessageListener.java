package galeev.authservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ObjectMapper objectMapper;
    @SneakyThrows
    @KafkaListener(topics = "output-message-topic", groupId = "group-id")
    public void messageHandler(String message) {
        Message messageObject = objectMapper.readValue(message, Message.class);
        log.info("{}", messageObject);
    }
}
