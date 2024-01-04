package galeev.prizeservice.service.processorImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.prizeservice.message.InputFromAuthServiceMessage;
import galeev.prizeservice.service.PrizeService;
import galeev.prizeservice.service.Processor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandProcessor implements Processor {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PrizeService prizeService;
    @Override
    public void processRequest(InputFromAuthServiceMessage inputFromWebhookServiceMessage) {

    }
}
