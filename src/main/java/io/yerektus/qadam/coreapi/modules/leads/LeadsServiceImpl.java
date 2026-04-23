package io.yerektus.qadam.coreapi.modules.leads;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.yerektus.qadam.coreapi.modules.leads.model.dto.CreateLeadBody;
import io.yerektus.qadam.coreapi.modules.leads.model.dto.LeadDto;
import io.yerektus.qadam.coreapi.modules.leads.model.entity.Lead;
import io.yerektus.qadam.coreapi.modules.leads.model.mapper.LeadMapper;
import reactor.core.publisher.Mono;

@Service
public class LeadsServiceImpl implements LeadsService {

    private final LeadsRepository leadsRepository;
    private final LeadMapper leadsMapper;

    public LeadsServiceImpl(LeadsRepository leadsRepository, LeadMapper leadsMapper) {
        this.leadsRepository = leadsRepository;
        this.leadsMapper = leadsMapper;
    }

    @Override
    public Mono<LeadDto> createLead(CreateLeadBody payload) {
        return this.leadsRepository.existsByEmail(payload.email())
            .flatMap((exists) -> {
                if (exists) {
                    return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered"
                        ));
                }

                Lead lead = new Lead();
                lead.setLastname(payload.lastname());
                lead.setFirstname(payload.firstname());
                lead.setEmail(payload.email());
                lead.setCity(payload.city());
                lead.setCompany(payload.company());
                lead.setOrganizationType(payload.organizationType());
                lead.setSource(payload.source());

                return leadsRepository.save(lead);
            })
            .map(leadsMapper::toDto);
    }

    @Override
    public Mono<List<LeadDto>> getLeads() {
        throw new UnsupportedOperationException("Unimplemented method 'getLeads'");
    }
    
}
