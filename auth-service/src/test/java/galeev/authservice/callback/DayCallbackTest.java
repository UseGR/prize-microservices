package galeev.authservice.callback;

import galeev.authservice.service.callbackImpl.DayCallback;
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
@DisplayName("Тест класса DayCallback")
public class DayCallbackTest {
    @Autowired
    private DayCallback dayCallback;
    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("callback/dayCallback/update.json"), Update.class);
    }

    @Test
    @DisplayName("Пользователь нажал клавижу выбора дня рождения")
    void chooseDayRequest() throws Exception {
        String response = loadResourceDataAsJson("callback/dayCallback/response.json").toString();

        Flux<? extends BotApiMethodMessage> request = dayCallback.handleCallback(update);

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
    @DisplayName("Пользователь нажал клавижу выбора дня рождения")
    void chooseDayFromUnregisteredUserRequest() throws Exception {
        this.update = objectMapper.readValue(loadResourceData("callback/dayCallback/update_404.json"), Update.class);

        Flux<? extends BotApiMethodMessage> request = dayCallback.handleCallback(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
