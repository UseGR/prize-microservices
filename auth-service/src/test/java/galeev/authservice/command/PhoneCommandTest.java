package galeev.authservice.command;

import galeev.authservice.entity.User;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.PhoneCommand;
import galeev.authservice.util.UserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static galeev.authservice.util.JsonUtils.loadResourceData;
import static galeev.authservice.util.JsonUtils.objectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Тест класса PhoneCommand")
public class PhoneCommandTest {
    @InjectMocks
    private PhoneCommand phoneCommand;

    @Mock
    private UserService userService;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("command/phoneCommand/update.json"), Update.class);
    }

    @Test
    @DisplayName("Пользователь отправил боту свой номер телефона")
    void phoneNumberInput() throws IOException {
        User user = UserBuilder.generateUserWithIdAndUsername();
        SendMessage response = objectMapper.readValue(loadResourceData("command/phoneCommand/response.json"), SendMessage.class);

        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userService.updateUserAndCheckEmptyFields(user, update, "Номер телефона сохранен"))
                .thenReturn(Mono.just(response));

        Flux<? extends BotApiMethodMessage> request = phoneCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message, response);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Незарегистрированный пользователь отправил боту свой номер телефона")
    void phoneKeyPressedByUnregisteredUser() {
        when(userService.findById(any())).thenReturn(Mono.empty());

        Flux<? extends BotApiMethodMessage> request = phoneCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
