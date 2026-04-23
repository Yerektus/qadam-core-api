package io.yerektus.qadam.coreapi.modules.leads;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.yerektus.qadam.coreapi.modules.leads.model.dto.CreateLeadBody;
import io.yerektus.qadam.coreapi.modules.leads.model.dto.LeadResponse;
import io.yerektus.qadam.coreapi.modules.leads.model.mapper.LeadMapper;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/leads")
public class LeadsController {

    private final LeadsService leadsService;
    private final LeadMapper leadMapper;

    public LeadsController(LeadsService leadsService, LeadMapper leadMapper) {
        this.leadsService = leadsService;
        this.leadMapper = leadMapper;
    }

    @PostMapping
    public Mono<LeadResponse> createLead(@Valid @RequestBody CreateLeadBody body) {
        var lead = leadsService.createLead(body);

        return lead.map(leadMapper::toResponse);
    }

    @GetMapping
    public Mono<List<LeadResponse>> getLeads() {
        var leads = leadsService.getLeads();

        return leads.map(leadsList -> leadsList.stream().map(leadMapper::toResponse).toList());
    }
}
