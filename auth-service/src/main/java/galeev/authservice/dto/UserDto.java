package galeev.authservice.dto;

import galeev.authservice.entity.User;

public record UserDto(Long id,
                      String username,
                      String fullname,
                      String phoneNumber,
                      String dateOfBirth,
                      boolean isParticipated,
                      User.Sex sex,
                      User.KnowFrom knowFrom,
                      boolean isAdmin
) {

    @Override
    public String toString() {
        return String.format("Ник: @%s%n" +
                        "ФИО: %s%n" +
                        "Номер телефона: %s%n" +
                        "Дата рождения: %s%n" +
                        "Выиграл(а) в розыгрыше: %s%n" +
                        "Пол: %s%n" +
                        "Узнал(а) о мероприятии из: %s%n" +
                        "Является администратором: %s",
                username,
                fullname,
                phoneNumber,
                dateOfBirth,
                isParticipated ? "Да" : "Нет",
                sex == User.Sex.MALE ? "Мужской" : "Женский",
                knowFrom.toString(),
                isAdmin ? "Да" : "Нет");
    }
}
