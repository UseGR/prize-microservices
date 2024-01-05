package galeev.prizeservice.service.processorImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.prizeservice.message.InputFromAuthServiceMessage;
import galeev.prizeservice.message.OutputToWebhookServiceMessage;
import galeev.prizeservice.service.Handler;
import galeev.prizeservice.service.PrizeService;
import galeev.prizeservice.service.Processor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


@Service
public class MediaProcessor implements Processor {
    private final Map<String, Handler> map;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PrizeService prizeService;

    public MediaProcessor(List<Handler> list,
                          ObjectMapper objectMapper,
                          KafkaTemplate<String, String> kafkaTemplate,
                          PrizeService prizeService) {
        this.map = list.stream().collect(toMap(Handler::getType, Function.identity()));
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.prizeService = prizeService;
    }

    @Override
    public void processRequest(InputFromAuthServiceMessage inputFromWebhookServiceMessage) {
        Mono.just(inputFromWebhookServiceMessage)
                .subscribe(kafkaMessage -> {
                    Update update = kafkaMessage.update();
                    if (update.getMessage().hasPhoto()) {
                        map.get("img").handleCommand(update)
                                .subscribe(message -> {
                                    OutputToWebhookServiceMessage outputToWebhookServiceMessage =
                                            new OutputToWebhookServiceMessage((SendMessage) message);
                                    try {
                                        kafkaTemplate.send("input-prize-service-message-topic",
                                                objectMapper.writeValueAsString(outputToWebhookServiceMessage));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    } else if (update.getMessage().hasAnimation()) {

                    }
                });
    }
}
