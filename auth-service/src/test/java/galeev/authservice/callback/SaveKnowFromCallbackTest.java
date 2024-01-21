package galeev.authservice.callback;

import galeev.authservice.repository.UserRepository;
import galeev.authservice.service.callbackImpl.SaveKnowFromCallback;
import galeev.authservice.util.UserBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static galeev.authservice.util.JsonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Тест класса SaveKnowFromCallback")
public class SaveKnowFromCallbackTest {
    @Autowired
    private SaveKnowFromCallback saveKnowFromCallback;

    @Autowired
    private UserRepository userRepository;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        userRepository.save(UserBuilder.generateUserWithIdAndUsername()).subscribe();
        userRepository.save(UserBuilder.generateUserWithAllFields1()).subscribe();
        update = objectMapper.readValue(loadResourceData("callback/saveKnowFromCallback/update.json"), Update.class);
    }

    @AfterEach
    void destroy() {
        userRepository.deleteAll().subscribe();
    }

    @Test
    @DisplayName("Пользователь нажал клавижу сохранения источника мероприятия")
    void knowFromSaveRequest() throws Exception {
        String response = loadResourceDataAsJson("callback/saveKnowFromCallback/response.json").toString();

        Flux<? extends BotApiMethodMessage> request = saveKnowFromCallback.handleCallback(update);

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
    @DisplayName("Незарегистрированный пользователь нажал клавижу сохранения источника мероприятия")
    void saveKnowFromFromUnregisteredUserRequest() throws Exception {
        this.update = objectMapper.readValue(loadResourceData("callback/saveKnowFromCallback/update_404.json"), Update.class);

        Flux<? extends BotApiMethodMessage> request = saveKnowFromCallback.handleCallback(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
