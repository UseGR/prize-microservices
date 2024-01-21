package galeev.authservice.callback;

import galeev.authservice.dto.DobDto;
import galeev.authservice.repository.UserRepository;
import galeev.authservice.service.DateOfBirthService;
import galeev.authservice.service.callbackImpl.MonthCallback;
import galeev.authservice.util.UserBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static galeev.authservice.util.JsonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Тест класса MonthCallback")
public class MonthCallbackTest {
    @Autowired
    private MonthCallback monthCallback;

    @MockBean
    private DateOfBirthService dateOfBirthService;

    @Autowired
    private UserRepository userRepository;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        userRepository.save(UserBuilder.generateUserWithIdAndUsername()).subscribe();
        userRepository.save(UserBuilder.generateUserWithAllFields1()).subscribe();
        update = objectMapper.readValue(loadResourceData("callback/monthCallback/update.json"), Update.class);
    }

    @AfterEach
    void destroy() {
        userRepository.deleteAll().subscribe();
    }

    @Test
    @DisplayName("Пользователь нажал клавижу выбора месяца рождения")
    void chooseMonthRequest() throws Exception {
        String response = loadResourceDataAsJson("callback/monthCallback/response.json").toString();
        Class<? extends DateOfBirthService> aClass = dateOfBirthService.getClass();

        Field dobCache = aClass.getDeclaredField("dobCache");
        dobCache.setAccessible(true);
        dobCache.set(dateOfBirthService, List.of(new DobDto(200157344L, "16", DobDto.DateType.DAY)));

        Flux<? extends BotApiMethodMessage> request = monthCallback.handleCallback(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendMessage message = (SendMessage) mes;
                    try {
                        String actual = objectMapper.writeValueAsString(message);
                        assertEquals(response, actual);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Незарегистрированный пользователь нажал клавижу выбора месяца рождения")
    void chooseMonthFromUnregisteredUserRequest() throws Exception {
        this.update = objectMapper.readValue(loadResourceData("callback/monthCallback/update_404.json"), Update.class);

        Flux<? extends BotApiMethodMessage> request = monthCallback.handleCallback(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
