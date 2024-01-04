package galeev.authservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputFromWebhookServiceMessage;
import galeev.authservice.message.OutputToWebhookServiceMessage;
import galeev.authservice.service.Callback;
import galeev.authservice.service.Processor;
import galeev.authservice.util.UserFieldChecker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CallbackProcessor implements Processor {
    private final Map<String, Callback> map;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserFieldChecker userFieldChecker;

    public CallbackProcessor(List<Callback> list, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, UserFieldChecker userFieldChecker) {
        this.map = list.stream().collect(Collectors.toMap(Callback::getType, Function.identity()));
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userFieldChecker = userFieldChecker;
    }

    @Override
    public void processRequest(InputFromWebhookServiceMessage inputFromWebhookServiceMessage) {
        Mono.just(inputFromWebhookServiceMessage)
                .map(InputFromWebhookServiceMessage::update)
                .subscribe(update -> {
                    Callback callback = map.entrySet().stream()
                            .filter(entry -> update.getCallbackQuery().getData().contains(entry.getKey())
                                    || update.getCallbackQuery().getData().startsWith(entry.getKey())
                                    || update.getCallbackQuery().getData().endsWith(entry.getKey())
                                    || update.getCallbackQuery().getData().matches(entry.getKey()))
                            .map(Map.Entry::getValue)
                            .findAny()
                            .orElseThrow();

                    callback.handleCallback(update)
                            .subscribe(message -> {
                                OutputToWebhookServiceMessage outputToWebhookServiceMessage = new OutputToWebhookServiceMessage(message, null);
                                try {
                                    kafkaTemplate.send("input-callback-topic", objectMapper.writeValueAsString(outputToWebhookServiceMessage));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                if (callback.getClass().getName().contains("Month") ||
                                        callback.getClass().getName().contains("Gender") ||
                                        callback.getClass().getName().contains("KnowFrom")
                                ) {
                                    userFieldChecker.isRegistrationComplete(inputFromWebhookServiceMessage.update());
                                }
                            });
                });
    }
}
