package io.yerektus.qadam.coreapi.modules.auth.model.mapper;

import org.mapstruct.Mapper;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.UserDto;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.UserResponse;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    UserResponse toResponse(User user);
}
