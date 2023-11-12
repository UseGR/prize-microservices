package galeev.authservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputMessage;
import galeev.authservice.message.OutputMessage;
import galeev.authservice.service.Command;
import galeev.authservice.service.Processor;
import galeev.authservice.service.UserService;
import galeev.authservice.util.UserFieldChecker;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandProcessor implements Processor {
    private final UserFieldChecker userFieldChecker;
    private final UserService userService;
    private final Map<String, Command> map;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CommandProcessor(UserFieldChecker userFieldChecker, UserService userService, List<Command> list, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userFieldChecker = userFieldChecker;
        this.userService = userService;
        this.map = list.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public void processRequest(InputMessage inputMessage) {
        Mono.just(inputMessage)
                .map(InputMessage::update)
                .subscribe(update -> {
                    Command command = map.entrySet().stream()
                            .filter(entry -> update.getMessage().getText().contains(entry.getKey())
                                    || update.getMessage().getText().matches(entry.getKey())
                                    || update.getMessage().getText().startsWith(entry.getKey()))
                            .map(Map.Entry::getValue).findAny().orElseThrow();

                    command.handleCommand(update)
                            .subscribe(message -> {
                                OutputMessage outputMessage = new OutputMessage(message, null);
                                try {
                                    kafkaTemplate.send("input-message-topic", objectMapper.writeValueAsString(outputMessage));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }
}
