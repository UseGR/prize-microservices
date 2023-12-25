package galeev.authservice.service;

import galeev.authservice.dto.DobDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Тест сервиса пользователей")
public class DateOfBirthServiceTest {
    @Autowired
    private DateOfBirthService dobService;
    private final List<DobDto> dobCache = new CopyOnWriteArrayList<>() {{
        add(new DobDto(200157344L, "day@16", DobDto.DateType.DAY));
    }};

    @Test
    @DisplayName("Метод формирует строку даты рождения из данных")
    void getFullDateOfBirth() throws Exception {
        Class<? extends DateOfBirthService> aClass = dobService.getClass();
        Field dobCacheField = aClass.getDeclaredField("dobCache");
        dobCacheField.setAccessible(true);
        dobCacheField.set(dobService, dobCache);

        String fullDateOfBirth = dobService.getFullDateOfBirth(200157344L, "month@oct");

        assertEquals("Октябрь, 16", fullDateOfBirth);
    }
}
