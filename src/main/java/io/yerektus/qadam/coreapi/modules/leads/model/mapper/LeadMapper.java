package io.yerektus.qadam.coreapi.modules.leads.model.mapper;

import org.mapstruct.Mapper;

import io.yerektus.qadam.coreapi.modules.leads.model.dto.LeadDto;
import io.yerektus.qadam.coreapi.modules.leads.model.dto.LeadResponse;
import io.yerektus.qadam.coreapi.modules.leads.model.entity.Lead;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    LeadResponse toResponse(LeadDto dto);
    LeadDto toDto(Lead payload);
}
