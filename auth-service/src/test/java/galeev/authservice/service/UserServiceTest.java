package galeev.authservice.service;

import galeev.authservice.entity.User;
import galeev.authservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Тест сервиса пользователей")
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Поиск пользователя по id - 200")
    void shouldGetUser() {
        User user = new User();
        user.setId(123L);
        user.setUsername("Rus_temM");

        when(userRepository.findById(123L)).thenReturn(Mono.just(user));

        Mono<User> userMono = userService.findById(123L);
        StepVerifier.
                create(userMono)
                .consumeNextWith(newUser -> {
                    assertEquals("Rus_temM", newUser.getUsername());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск пользователя по id - 404")
    void shouldNotGetUser() {
        when(userRepository.findById(123L)).thenReturn(Mono.empty());

        Mono<User> userMono = userService.findById(123L);
        StepVerifier
                .create(userMono)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Сохранение пользователя - 200")
    void shouldSaveUser() {
        User user = new User();
        user.setId(123L);
        user.setUsername("Rus_temM");

        when(userRepository.save(user)).thenReturn(Mono.just(user));

        Mono<User> userMono = userService.save(user);
        StepVerifier.
                create(userMono)
                .consumeNextWith(savedUser -> {
                    assertEquals("Rus_temM", savedUser.getUsername());
                    assertEquals(123L, savedUser.getId());
                })
                .verifyComplete();
    }
}
