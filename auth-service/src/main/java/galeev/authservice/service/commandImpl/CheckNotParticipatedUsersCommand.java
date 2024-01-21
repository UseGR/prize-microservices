package galeev.authservice.service.commandImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.message.OutputToPrizeServiceMessage;
import galeev.authservice.service.Command;
import galeev.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckNotParticipatedUsersCommand implements Command {
    private final UserService userService;
    @Value(value = "${application.admin.id}")
    private String adminId;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<PartialBotApiMethod<? extends Serializable>> handleCommand(Update update) {
        return userService.findAll()
                .filter(user -> !user.getIsAdmin() && !user.getIsParticipated())
                .collectList()
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        return Flux.just(SendMessage.builder()
                                .chatId(adminId)
                                .text("Не осталось не участвовавших в розыгрыше гостей")
                                .build());
                    } else {
                        OutputToPrizeServiceMessage outputToPrizeServiceMessage =
                                new OutputToPrizeServiceMessage(
                                        update.getMessage().getChatId(),
                                        update,
                                        OutputToPrizeServiceMessage.MessageType.MESSAGE
                                );
                        try {
                            kafkaTemplate.send("output-prize-service-message-topic",
                                    objectMapper.writeValueAsString(outputToPrizeServiceMessage));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        return Flux.empty();
                    }
                });
    }

    @Override
    public String getType() {
        return "Разыграть призы";
    }
}
