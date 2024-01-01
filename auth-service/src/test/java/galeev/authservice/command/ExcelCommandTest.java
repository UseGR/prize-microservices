package galeev.authservice.command;

import galeev.authservice.service.commandImpl.ExcelCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.Serializable;

import static galeev.authservice.util.JsonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Тест класса ExcelCommand")
public class ExcelCommandTest {
    @Autowired
    private ExcelCommand excelCommand;

    private Update update;

    @BeforeEach
    void setUp() throws IOException {
        update = objectMapper.readValue(loadResourceData("command/excelCommand/update.json"), Update.class);
    }

    @Test
    @DisplayName("Администратор запросил выгрузку")
    void getExcelTableRequest() throws Exception {
        JsonNode response = loadResourceDataAsJson("command/excelCommand/response.json");
        Flux<PartialBotApiMethod<? extends Serializable>> request = excelCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .consumeNextWith(mes -> {
                    SendDocument message = (SendDocument) mes;
                    try {
                        JsonNode actual = convertStringToJson(objectMapper.writeValueAsString(message));
                        assertEquals(response, actual);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Обычный пользователь запросил выгрузку")
    void getExcelTableByUsualUSerRequest() throws Exception {
        this.update = objectMapper.readValue(loadResourceData("command/excelCommand/bad_update.json"), Update.class);
        Flux<PartialBotApiMethod<? extends Serializable>> request = excelCommand.handleCommand(update);

        StepVerifier
                .create(request)
                .expectComplete()
                .verify();
    }
}
