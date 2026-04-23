package io.yerektus.qadam.coreapi.modules.generation.controller;

import io.yerektus.qadam.coreapi.modules.generation.dto.GenerationRequest;
import io.yerektus.qadam.coreapi.modules.generation.dto.GenerationResponse;
import io.yerektus.qadam.coreapi.modules.generation.service.GenerationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/generate")
public class GenerationController {

    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/document")
    public Mono<GenerationResponse> generateDocument(@Valid @RequestBody GenerationRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UUID.class)
                .flatMap(userId -> generationService.generate(userId, request));
    }
}
