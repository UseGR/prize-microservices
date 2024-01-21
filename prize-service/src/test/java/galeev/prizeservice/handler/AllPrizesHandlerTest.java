package galeev.prizeservice.handler;

import galeev.prizeservice.entity.Prize;
import galeev.prizeservice.service.PrizeService;
import galeev.prizeservice.service.handlerImpl.AllPrizesHandler;
import galeev.prizeservice.util.PrizeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static galeev.prizeservice.util.JsonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тест класса AllPrizesHandler")
public class AllPrizesHandlerTest {
    @Autowired
    private AllPrizesHandler allPrizesHandler;

    @Mock
    private PrizeService prizeService;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("handler/allPrizesHandler/update.json"), Update.class);
    }

    @Test
    @DisplayName("Администратор запросил список лотов")
    void allPrizesRequest() throws IOException {
        List<Prize> prizeList = PrizeBuilder.generatePrizes();
        String response1 = loadResourceDataAsJson("handler/allPrizesHandler/response1.json").toString();
        String response2 = loadResourceDataAsJson("handler/allPrizesHandler/response2.json").toString();


        when(prizeService.findAll()).thenReturn(Flux.fromIterable(prizeList));

        Flux<PartialBotApiMethod<? extends Serializable>> request = allPrizesHandler.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendPhoto message = (SendPhoto) mes;
                    try {
                        String actual = objectMapper.writeValueAsString(message);
                        assertEquals(response1, actual);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .consumeNextWith(mes -> {
                    SendAnimation message = (SendAnimation) mes;
                    try {
                        String actual = objectMapper.writeValueAsString(message);
                        assertEquals(response2, actual);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }
}
