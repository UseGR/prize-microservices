package galeev.prizeservice.dto;

import java.util.UUID;

public record PrizeRequestDto(UUID id, String adminDescription, String winnerDescription) {
}
