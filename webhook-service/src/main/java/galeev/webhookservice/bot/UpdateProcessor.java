package galeev.webhookservice.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.webhookservice.message.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void processInputUpdate(Update update) {
        if (update.hasMessage()) {
            OutputMessage outputMessage = new OutputMessage(update.getMessage().getChatId(), update, OutputMessage.MessageType.MESSAGE);
            kafkaTemplate.send("output-message-topic", objectMapper.writeValueAsString(outputMessage));
        }

        if (update.hasCallbackQuery()) {
            OutputMessage callback = new OutputMessage(update.getMessage().getChatId(), update, OutputMessage.MessageType.CALLBACK);
            kafkaTemplate.send("output-callback-topic", objectMapper.writeValueAsString(callback));
        }
    }
}
