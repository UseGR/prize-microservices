package galeev.authservice.mapper;

import galeev.authservice.dto.UserDto;
import galeev.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);
}
