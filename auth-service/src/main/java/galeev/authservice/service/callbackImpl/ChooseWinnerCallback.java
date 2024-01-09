package galeev.authservice.service.callbackImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import galeev.authservice.dto.WinnerDto;
import galeev.authservice.entity.User;
import galeev.authservice.mapper.UserMapper;
import galeev.authservice.service.Callback;
import galeev.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseWinnerCallback implements Callback {
    private final UserService userService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Override
    public Flux<? extends BotApiMethodMessage> handleCallback(Update update) {
        String prizeId = update.getCallbackQuery().getData().replace("rollPrize@", "").trim();
        return userService.findAll()
                .filter(user -> !user.getIsParticipated() && !user.getIsAdmin())
                .collectList()
                .flatMapMany(users -> {
                    if (!users.isEmpty()) {
                        List<User> userList = new ArrayList<>(users);
                        Collections.shuffle(userList);
                        User winner = userList.get(0);
                        winner.setIsParticipated(true);
                        userService.save(winner)
                                .subscribe(user -> {
                                    try {
                                        WinnerDto winnerDto = userMapper.toWinnerDto(winner);
                                        winnerDto.setPrizeId(prizeId);
                                        kafkaTemplate.send("choose-prize-for-winner-topic",
                                                objectMapper.writeValueAsString(winnerDto));
                                    } catch (JsonProcessingException e) {
                                        log.error("Error during sending userDto to prize-service to set him prize in ChooseWinnerCallback");
                                        throw new RuntimeException(e);
                                    }
                                });
                    }
                    return Flux.empty();
                });
    }

    @Override
    public String getType() {
        return "rollPrize@";
    }
}
