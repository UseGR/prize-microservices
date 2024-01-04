package galeev.prizeservice.dto;

import java.util.UUID;
public record PrizeResponseDto(UUID id, String adminDescription, String winnerDescription) {
}
