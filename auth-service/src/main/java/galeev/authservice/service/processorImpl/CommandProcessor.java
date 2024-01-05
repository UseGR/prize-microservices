package galeev.authservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.entity.User;
import galeev.authservice.message.InputFromWebhookServiceMessage;
import galeev.authservice.message.OutputToPrizeServiceMessage;
import galeev.authservice.message.OutputToWebhookServiceMessage;
import galeev.authservice.service.Command;
import galeev.authservice.service.Processor;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.FullnameCommand;
import galeev.authservice.util.UserFieldChecker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class CommandProcessor implements Processor {
    private final Map<String, Command> map;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserFieldChecker userFieldChecker;
    private final List<String> adminCommands = List.of("Сформировать лот для розыгрыша");

    public CommandProcessor(List<Command> list,
                            KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper objectMapper,
                            UserService userService,
                            UserFieldChecker userFieldChecker) {
        this.map = list.stream().collect(toMap(Command::getType, Function.identity()));
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.userFieldChecker = userFieldChecker;
    }


    @Override
    public void processRequest(InputFromWebhookServiceMessage inputFromWebhookServiceMessage) {
        Mono.just(inputFromWebhookServiceMessage)
                .map(InputFromWebhookServiceMessage::update)
                .subscribe(update -> {
                    executeAdminCommand(update);

                    if (update.getMessage().hasText() && !adminCommands.contains(update.getMessage().getText())) {
                        Command command = map.entrySet().stream()
                                .filter(entry -> update.getMessage().getText().contains(entry.getKey())
                                        || update.getMessage().getText().matches(entry.getKey())
                                        || update.getMessage().getText().startsWith(entry.getKey()))
                                .map(Map.Entry::getValue).findAny().orElse(new FullnameCommand(userService));

                        command.handleCommand(update)
                                .subscribe(message -> {
                                    if (message instanceof SendMessage) {
                                        OutputToWebhookServiceMessage outputToWebhookServiceMessage =
                                                new OutputToWebhookServiceMessage((SendMessage) message, null);
                                        try {
                                            kafkaTemplate.send("input-message-topic",
                                                    objectMapper.writeValueAsString(outputToWebhookServiceMessage));
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }

                                        if (command.getClass().getName().contains("Phone") ||
                                                command.getClass().getName().contains("Username") ||
                                                command.getClass().getName().contains("Fullname")
                                        ) {
                                            userFieldChecker.isRegistrationComplete(inputFromWebhookServiceMessage.update());
                                        }
                                    } else {
                                        OutputToWebhookServiceMessage outputToWebhookServiceMessage =
                                                new OutputToWebhookServiceMessage(null, (SendDocument) message);
                                        try {
                                            kafkaTemplate.send("input-message-topic",
                                                    objectMapper.writeValueAsString(outputToWebhookServiceMessage));
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                    }
                });
    }

    private void executeAdminCommand(Update update) {
        userFieldChecker.checkUserFields(update).subscribe(fields ->
                userService.findById(update.getMessage().getChatId())
                        .subscribe(user -> {
                            if (checkUserIsAdmin(user, fields, update)) {
                                OutputToPrizeServiceMessage outputToPrizeServiceMessage =
                                        new OutputToPrizeServiceMessage(
                                                update.getMessage().getChatId(),
                                                update,
                                                update.getMessage().hasText() ?
                                                        OutputToPrizeServiceMessage.MessageType.MESSAGE :
                                                        OutputToPrizeServiceMessage.MessageType.MEDIA
                                        );
                                try {
                                    kafkaTemplate.send("output-prize-service-message-topic",
                                            objectMapper.writeValueAsString(outputToPrizeServiceMessage));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }));
    }

    private boolean checkUserIsAdmin(User user, List<Map<String, String>> fields, Update update) {
        return user.getIsAdmin().equals(true) &&
                fields.isEmpty() &&
                (update.getMessage().getText() == null || adminCommands.contains(update.getMessage().getText()));
    }
}
