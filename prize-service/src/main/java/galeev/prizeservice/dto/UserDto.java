package galeev.prizeservice.dto;


public record UserDto(Long id,
                      String fullname,
                      String username,
                      String phoneNumber,
                      String prizeId
) { }
