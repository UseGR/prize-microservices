package galeev.prizeservice.handler;

import galeev.prizeservice.repository.PrizeRepository;
import galeev.prizeservice.service.handlerImpl.AllPrizesHandler;
import galeev.prizeservice.util.PrizeBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.Serializable;

import static galeev.prizeservice.util.JsonUtils.loadResourceData;
import static galeev.prizeservice.util.JsonUtils.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Тест класса AllPrizesHandler")
public class AllPrizesHandlerTest {
    @Autowired
    private AllPrizesHandler allPrizesHandler;

    @Autowired
    private PrizeRepository prizeRepository;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        prizeRepository.save(PrizeBuilder.generatePrizes().get(0)).subscribe();
        prizeRepository.save(PrizeBuilder.generatePrizes().get(1)).subscribe();
        update = objectMapper.readValue(loadResourceData("handler/allPrizesHandler/update.json"), Update.class);
    }

    @AfterEach
    void destroy() {
        prizeRepository.deleteAll().subscribe();
    }

    @Test
    @DisplayName("Администратор запросил список лотов")
    void allPrizesRequest() {
        Flux<PartialBotApiMethod<? extends Serializable>> request = allPrizesHandler.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendPhoto message = (SendPhoto) mes;
                    assertThat(message.getCaption()).isNotBlank();
                    assertThat(message.getMethod()).isEqualTo("sendphoto");
                })
                .consumeNextWith(mes -> {
                    SendAnimation message = (SendAnimation) mes;
                    assertThat(message.getCaption()).isNotBlank();
                    assertThat(message.getMethod()).isEqualTo("sendAnimation");
                })
                .verifyComplete();
    }
}
