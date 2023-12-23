package galeev.authservice.util;

import galeev.authservice.dto.UserDto;
import galeev.authservice.entity.User;

public class UserBuilder {
    public static User generateUserWithIdAndUsername() {
        User user = new User();
        user.setId(200157344L);
        user.setUsername("Rus_temM");

        return user;
    }

    public static User generateUserWithAllFields1() {
        return new User(200157344L,
                "Рустем",
                "Rus_teM",
                "Галеев Рустем Ильсурович",
                "89991627212",
                "Октябрь, 16",
                false,
                User.Sex.MALE,
                User.KnowFrom.TELEGRAM,
                true);
    }
    public static User generateUserWithAllFields2() {
        return new User(200157345L,
                "Бот",
                "Bob",
                "Bob Smith",
                "12349874756",
                "Ноябрь, 18",
                false,
                User.Sex.MALE,
                User.KnowFrom.WHATSAPP,
                false);
    }
    public static User generateUserWithAllFields3() {
        return new User(200157346L,
                "Kate",
                "KaTe",
                "Kate Addams",
                "12359277751",
                "Февраль, 14",
                false,
                User.Sex.FEMALE,
                User.KnowFrom.INSTAGRAM,
                false);
    }

    public static UserDto generateUserDto1() {
        return new UserDto(
                200157344L,
                "Rus_teM",
                "Галеев Рустем Ильсурович",
                "89991627212",
                "Октябрь, 16",
                false,
                User.Sex.MALE,
                User.KnowFrom.TELEGRAM,
                true
        );
    }

    public static UserDto generateUserDto2() {
        return new UserDto(
                200157345L,
                "KaTe",
                "Kate Addams",
                "12359277751",
                "Ноябрь, 18",
                false,
                User.Sex.MALE,
                User.KnowFrom.WHATSAPP,
                false
        );
    }
    public static UserDto generateUserDto3() {
        return new UserDto(
                200157346L,
                "BoB",
                "Bob Smith",
                "12349874756",
                "Февраль, 14",
                false,
                User.Sex.FEMALE,
                User.KnowFrom.TELEGRAM,
                false
        );
    }
}
