package galeev.authservice.command;

import galeev.authservice.entity.User;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.StartCommand;
import galeev.authservice.util.UserBuilder;
import galeev.authservice.util.UserFieldChecker;
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
import java.lang.reflect.Field;
import java.util.Collections;

import static galeev.authservice.util.JsonUtils.loadResourceData;
import static galeev.authservice.util.JsonUtils.objectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
@DisplayName("Тест класса StartCommand")
public class StartCommandTest {
    @InjectMocks
    private StartCommand startCommand;

    @Mock
    private UserService userService;

    @Mock
    private UserFieldChecker userFieldChecker;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("command/startCommand/update.json"), Update.class);
    }

    @Test
    @DisplayName("В телеграм боте нажата клавиша /start")
    void startKeyPressed() throws Exception {
        User user = UserBuilder.generateUserWithIdAndUsername();

        when(userService.findById(any())).thenReturn(Mono.empty());
        when(userService.save(any())).thenReturn(Mono.just(user));

        Class<? extends StartCommand> aClass = startCommand.getClass();
        Field adminId = aClass.getDeclaredField("adminId");
        adminId.setAccessible(true);
        adminId.set(startCommand, "200157344");

        Flux<PartialBotApiMethod<? extends Serializable>> request = startCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message.getChatId(), "200157344");
                    assertEquals(message.getText(), "Зарегистрируйтесь, пожалуйста");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("До конца не зарегистрированный пользователь нажал клавишу /start")
    void startKeyPressedByUnregistered() {
        User user = UserBuilder.generateUserWithIdAndUsername();
        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userFieldChecker.checkUserFields(update)).thenReturn(Mono.just(Collections.singletonList(any())));

        Flux<PartialBotApiMethod<? extends Serializable>> request = startCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message.getChatId(), "200157344");
                    assertEquals(message.getText(), "Пожалуйста, завершите регистрацию");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Зарегистрированный пользователь нажал клавишу /start")
    void startKeyPressedByRegistered() {
        User user = UserBuilder.generateUserWithAllFields1();
        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userFieldChecker.checkUserFields(update)).thenReturn(Mono.just(Collections.emptyList()));

        Flux<PartialBotApiMethod<? extends Serializable>> request = startCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message.getChatId(), "200157344");
                    assertEquals(message.getText(), "Привет, " + user.getFirstname());
                })
                .verifyComplete();
    }
}
