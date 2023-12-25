package galeev.authservice.callback;

import galeev.authservice.service.callbackImpl.ChooseKnowFromCallback;
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
@DisplayName("Тест класса ChooseKnowFromCallback")
public class ChooseKnowFromCallbackTest {
    @Autowired
    private ChooseKnowFromCallback knowFromCallback;
    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("callback/chooseKnowFromCallback/update.json"), Update.class);
    }

    @Test
    @DisplayName("Пользователь нажал клавижу выбора источника информации о мероприятии")
    void knowFromRequest() throws Exception {
        String response = loadResourceDataAsJson("callback/chooseKnowFromCallback/response.json").toString();

        Flux<? extends BotApiMethodMessage> request = knowFromCallback.handleCallback(update);

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
}
