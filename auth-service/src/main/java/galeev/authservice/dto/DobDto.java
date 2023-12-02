package galeev.authservice.dto;

public record DobDto(Long id, String data, DateType dateType) {
    public enum DateType {
        DAY,
        MONTH
    }
}
