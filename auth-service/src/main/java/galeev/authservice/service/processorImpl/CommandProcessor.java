package galeev.authservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.InputMessage;
import galeev.authservice.message.OutputMessage;
import galeev.authservice.service.Command;
import galeev.authservice.service.Processor;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.FullnameCommand;
import galeev.authservice.util.UserFieldChecker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandProcessor implements Processor {
    private final Map<String, Command> map;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserFieldChecker userFieldChecker;

    public CommandProcessor(List<Command> list, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, UserService userService, UserFieldChecker userFieldChecker) {
        this.map = list.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userFieldChecker = userFieldChecker;
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
                            .map(Map.Entry::getValue).findAny().orElse(new FullnameCommand(userService));

                    command.handleCommand(update)
                            .subscribe(message -> {
                                OutputMessage outputMessage = new OutputMessage(message, null);
                                try {
                                    kafkaTemplate.send("input-message-topic", objectMapper.writeValueAsString(outputMessage));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                if (command.getClass().getName().contains("Phone") ||
                                        command.getClass().getName().contains("Username") ||
                                        command.getClass().getName().contains("Fullname")
                                ) {
                                    userFieldChecker.isRegistrationComplete(inputMessage.update());
                                }
                            });
                });
    }
}
