package io.yerektus.qadam.coreapi.modules.leads;

import java.util.List;

import org.springframework.stereotype.Service;

import io.yerektus.qadam.coreapi.modules.leads.model.dto.CreateLeadBody;
import io.yerektus.qadam.coreapi.modules.leads.model.dto.LeadDto;
import reactor.core.publisher.Mono;

@Service
public interface LeadsService {
    Mono<LeadDto> createLead(CreateLeadBody body);
    Mono<List<LeadDto>> getLeads();
}
