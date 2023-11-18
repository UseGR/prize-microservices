package galeev.authservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputMessage;
import galeev.authservice.message.OutputMessage;
import galeev.authservice.service.Callback;
import galeev.authservice.service.Processor;
import galeev.authservice.service.UserService;
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
    private final UserFieldChecker userFieldChecker;
    private final UserService userService;
    private final Map<String, Callback> map;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CallbackProcessor(UserFieldChecker userFieldChecker, UserService userService, List<Callback> list, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userFieldChecker = userFieldChecker;
        this.userService = userService;
        this.map = list.stream().collect(Collectors.toMap(Callback::getType, Function.identity()));
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void processRequest(InputMessage inputMessage) {
        Mono.just(inputMessage)
                .map(InputMessage::update)
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
                                OutputMessage outputMessage = new OutputMessage(message, null);
                                try {
                                    kafkaTemplate.send("input-callback-topic", objectMapper.writeValueAsString(outputMessage));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }
}