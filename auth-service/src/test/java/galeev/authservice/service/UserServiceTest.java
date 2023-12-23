package galeev.authservice.service;

import galeev.authservice.dto.UserDto;
import galeev.authservice.entity.User;
import galeev.authservice.mapper.UserMapper;
import galeev.authservice.repository.UserRepository;
import galeev.authservice.util.UserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
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

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Поиск пользователя по id - 200")
    void shouldGetUser() {
        User user = UserBuilder.generateUserWithIdAndUsername();

        when(userRepository.findById(200157344L)).thenReturn(Mono.just(user));

        Mono<User> userMono = userService.findById(200157344L);
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
        User user = UserBuilder.generateUserWithIdAndUsername();

        when(userRepository.save(user)).thenReturn(Mono.just(user));

        Mono<User> userMono = userService.save(user);
        StepVerifier.
                create(userMono)
                .consumeNextWith(savedUser -> {
                    assertEquals("Rus_temM", savedUser.getUsername());
                    assertEquals(200157344L, savedUser.getId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск всех пользователей - 200")
    void shouldReturnUsersList() {
        User user1 = UserBuilder.generateUserWithAllFields1();
        User user2 = UserBuilder.generateUserWithAllFields2();
        User user3 = UserBuilder.generateUserWithAllFields3();

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2, user3));

        Flux<User> users = userService.findAll();

        StepVerifier.
                create(users)
                .expectNext(user1, user2, user3)
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск информации по всем пользователям - 200")
    void shouldReturnUserData() {
        User user1 = UserBuilder.generateUserWithAllFields1();
        User user2 = UserBuilder.generateUserWithAllFields2();
        User user3 = UserBuilder.generateUserWithAllFields3();

        UserDto userDto1 = UserBuilder.generateUserDto1();
        UserDto userDto2 = UserBuilder.generateUserDto2();
        UserDto userDto3 = UserBuilder.generateUserDto3();

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2, user3));
        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);
        when(userMapper.toDto(user3)).thenReturn(userDto3);

        StepVerifier
                .create(userService.getUsersData(200157344L))
                .expectNextCount(3)
                .verifyComplete();
    }

}
