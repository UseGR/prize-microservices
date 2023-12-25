package galeev.authservice.command;

import galeev.authservice.dto.UserDto;
import galeev.authservice.entity.User;
import galeev.authservice.mapper.UserMapper;
import galeev.authservice.service.UserService;
import galeev.authservice.service.commandImpl.AllUsersCommand;
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
@DisplayName("Тест класса AllUsersCommand")
public class AllUsersCommandTest {
    @InjectMocks
    private AllUsersCommand allUsersCommand;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("command/allUsersCommand/update.json"), Update.class);
    }

    @Test
    @DisplayName("Администратор запросил всех пользователей")
    void allUsersRequest() throws IOException {
        User user = UserBuilder.generateUserWithAllFields1();
        UserDto userDto = UserBuilder.generateUserDto1();
        SendMessage response = objectMapper.readValue(loadResourceData("command/allUsersCommand/response.json"), SendMessage.class);

        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userService.getUsersData(any()))
                .thenReturn(Flux.just(response));
        when(userMapper.toDto(user)).thenReturn(userDto);

        Flux<? extends BotApiMethodMessage> request = allUsersCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    assertEquals(message, response);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Обычный пользователь запросил всех пользователей")
    void notAdminAllUsersRequest() {
        User user = UserBuilder.generateUserWithAllFields2();
        UserDto userDto = UserBuilder.generateUserDto2();

        when(userService.findById(any())).thenReturn(Mono.just(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        Flux<? extends BotApiMethodMessage> request = allUsersCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Незарегистрированный пользователь запросил всех пользователей")
    void unregisteredUserAllUsersRequest() {
        when(userService.findById(any())).thenReturn(Mono.empty());

        Flux<? extends BotApiMethodMessage> request = allUsersCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
