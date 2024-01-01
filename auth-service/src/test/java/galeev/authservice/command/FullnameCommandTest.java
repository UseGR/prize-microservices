package galeev.authservice.command;

import galeev.authservice.entity.User;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.FullnameCommand;
import galeev.authservice.util.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.Serializable;

import static galeev.authservice.util.JsonUtils.loadResourceData;
import static galeev.authservice.util.JsonUtils.objectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Тест класса FullnameCommand")
public class FullnameCommandTest {
    @InjectMocks
    private FullnameCommand fullnameCommand;

    @Mock
    private UserService userService;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("command/fullnameCommand/update.json"), Update.class);
    }

    @Test
    @DisplayName("Пользователь ввел свое ФИО")
    void fioInput() throws IOException {
        User user = UserBuilder.generateUserWithIdAndUsername();
        SendMessage response = objectMapper.readValue(loadResourceData("command/fullnameCommand/response.json"), SendMessage.class);

        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userService.save(any())).thenReturn(Mono.just(user));
        when(userService.updateUserAndCheckEmptyFields(user, update, "ФИО сохранено"))
                .thenReturn(Mono.just(response));

        Flux<PartialBotApiMethod<? extends Serializable>> request = fullnameCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message, response);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Пользователь ввел некорректное ФИО")
    void fioBadInput() throws IOException {
        this.update = objectMapper.readValue(loadResourceData("command/fullnameCommand/update_bad_input.json"), Update.class);
        User user = UserBuilder.generateUserWithIdAndUsername();
        SendMessage response = objectMapper.readValue(loadResourceData("command/fullnameCommand/bad_response.json"), SendMessage.class);

        when(userService.findById(any())).thenReturn(Mono.just(user));

        Flux<PartialBotApiMethod<? extends Serializable>> request = fullnameCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message, response);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Пользователь с полным ФИО ввел новое ФИО")
    void fioAlreadyExistsInput() throws IOException {
        User user = UserBuilder.generateUserWithAllFields1();
        SendMessage response = objectMapper.readValue(loadResourceData("command/fullnameCommand/bad_response.json"), SendMessage.class);

        when(userService.findById(any())).thenReturn(Mono.just(user));

        Flux<PartialBotApiMethod<? extends Serializable>> request = fullnameCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message, response);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Незарегистрированный пользователь ввел ФИО")
    void unregisteredUserFioInput() {
        when(userService.findById(any())).thenReturn(Mono.empty());

        Flux<PartialBotApiMethod<? extends Serializable>> request = fullnameCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
