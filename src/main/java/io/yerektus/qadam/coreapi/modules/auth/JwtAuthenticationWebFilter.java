package io.yerektus.qadam.coreapi.modules.auth;

import io.yerektus.qadam.coreapi.modules.auth.repository.AccessTokenRepository;
import io.yerektus.qadam.coreapi.modules.auth.service.JwtService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final AccessTokenRepository accessTokenRepository;

    public JwtAuthenticationWebFilter(JwtService jwtService, AccessTokenRepository accessTokenRepository) {
        this.jwtService = jwtService;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String method = request.getMethod().name();
        String path = request.getPath().value();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("JwtAuthenticationWebFilter::filter skipped: missing bearer token method={} path={}", method, path);
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            log.warn("JwtAuthenticationWebFilter::filter rejected: invalid token method={} path={}", method, path);
            return chain.filter(exchange);
        }

        return accessTokenRepository.existsByToken(token).flatMap(exists -> {
            if (!exists) {
                log.warn("JwtAuthenticationWebFilter::filter rejected: token not found method={} path={}", method, path);
                return chain.filter(exchange);
            }

            UUID userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            log.debug("JwtAuthenticationWebFilter::filter authenticated userId={} role={} method={} path={}", userId, role, method, path);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        });
    }
}
