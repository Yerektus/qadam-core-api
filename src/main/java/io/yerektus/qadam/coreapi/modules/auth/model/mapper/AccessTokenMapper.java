package io.yerektus.qadam.coreapi.modules.auth.model.mapper;

import org.mapstruct.Mapper;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.AccessToken;

@Mapper(componentModel = "spring")
public interface AccessTokenMapper {
    AccessToken toEntity(AccessTokenDto dto);
    AccessTokenDto toDto(AccessToken entity);
}
