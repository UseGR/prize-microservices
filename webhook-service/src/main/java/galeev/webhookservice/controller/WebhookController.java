package galeev.webhookservice.controller;

import galeev.webhookservice.bot.UpdateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final UpdateProcessor updateProcessor;

    @PostMapping("/")
    public Mono<ResponseEntity<?>> onUpdateReceived(@RequestBody Update update) {
        updateProcessor.processInputUpdate(update);
        return Mono.just(ResponseEntity.ok().build());
    }
}
