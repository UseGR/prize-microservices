package galeev.prizeservice.dto;

public record PrizeDataDto(String prizeDescription, String winnerDescription, boolean isRolled, Long winnerId) {
    @Override
    public String toString() {
        return String.format("Описание лота для админа: %s%n" +
                "Описание лота для победителя: %s%n" +
                "Лот разыгран: %s%n" +
                "Победитель: %s",
                prizeDescription,
                winnerDescription,
                isRolled ? "Да" : "Нет",
                winnerId == null ? "Лот не разыгран" : winnerId);
    }
}
