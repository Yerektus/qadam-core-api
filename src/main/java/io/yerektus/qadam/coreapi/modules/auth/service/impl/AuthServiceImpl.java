package io.yerektus.qadam.coreapi.modules.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.AuthResponse;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.LoginBody;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.RegisterBody;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.UserResponse;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.AccessToken;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.User;
import io.yerektus.qadam.coreapi.modules.auth.model.mapper.AccessTokenMapper;
import io.yerektus.qadam.coreapi.modules.auth.model.mapper.UserMapper;
import io.yerektus.qadam.coreapi.modules.auth.repository.AccessTokenRepository;
import io.yerektus.qadam.coreapi.modules.auth.repository.UserRepository;
import io.yerektus.qadam.coreapi.modules.auth.service.AuthService;
import io.yerektus.qadam.coreapi.modules.auth.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AccessTokenMapper accessTokenMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper, AccessTokenRepository accessTokenRepository, AccessTokenMapper accessTokenMapper) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.accessTokenMapper = accessTokenMapper;
    }

    public Mono<AuthResponse> register(RegisterBody payload) {
        log.info("AuthServiceImpl::register started email={}", payload.email());

        return Mono.zip(
            userRepository.existsByEmail(payload.email()),
            userRepository.existsByPhoneNumber(payload.phoneNumber())
        )
                .flatMap(result -> {
                    boolean emailExists = result.getT1();
                    boolean phoneExists = result.getT2();

                    if (emailExists) {
                        log.warn("AuthServiceImpl::register rejected: email already registered email={}", payload.email());
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered"
                        ));
                    }

                    if (phoneExists) {
                        log.warn("AuthServiceImpl::register rejected: phone already registered email={}", payload.email());
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Phone already registered"
                        ));
                    }

                    User user = new User();
                    user.setEmail(payload.email());
                    user.setFirstname(payload.firstname());
                    user.setLastname(payload.lastname());
                    user.setPhoneNumber(payload.phoneNumber());
                    user.setPasswordHash(passwordEncoder.encode(payload.password()));
                    user.setRole("LAWYER");
                    user.setCreatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {
                    AccessTokenDto accessTokenDto = jwtService.generateAuthDto(savedUser.getId(), savedUser.getRole());

                    AccessToken accessToken = new AccessToken();
                    accessToken.setUserId(savedUser.getId());
                    accessToken.setToken(accessTokenDto.token());
                    accessToken.setExpiresAt(accessTokenDto.expiresAt());

                    return accessTokenRepository.save(accessToken).map(savedAccessToken -> {
                        log.info("AuthServiceImpl::register completed userId={} email={} role={}",
                                savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
                        return new AuthResponse(userMapper.toDto(savedUser), accessTokenMapper.toDto(accessToken));
                    });
                });
    }

    public Mono<AuthResponse> login(LoginBody payload) {
        log.info("AuthServiceImpl::login started email={}", payload.email());

        return userRepository.findByEmail(payload.email())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("AuthServiceImpl::login rejected: user not found email={}", payload.email());
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid email or password"
                    ));
                }))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(payload.password(), user.getPasswordHash())) {
                        log.warn("AuthServiceImpl::login rejected: invalid password userId={} email={}",
                                user.getId(), user.getEmail());
                            
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password"
                        ));
                    }

                    AccessTokenDto accessTokenDto = jwtService.generateAuthDto(user.getId(), user.getRole());

                    AccessToken accessToken = new AccessToken();
                    accessToken.setUserId(user.getId());
                    accessToken.setToken(accessTokenDto.token());
                    accessToken.setExpiresAt(accessTokenDto.expiresAt());

                    return accessTokenRepository.save(accessToken).map(savedAccessToken -> {
                        log.info("AuthServiceImpl::login completed userId={} email={} role={}",
                                user.getId(), user.getEmail(), user.getRole());
                        return new AuthResponse(userMapper.toDto(user), accessTokenMapper.toDto(accessToken));
                    });
                });
    }

    public Mono<UserResponse> getCurrentUser(UUID userId) {
        log.debug("AuthServiceImpl::getCurrentUser started userId={}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("AuthServiceImpl::getCurrentUser rejected: user not found userId={}", userId);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));
                }))
                .map(user -> {
                    log.debug("AuthServiceImpl::getCurrentUser completed userId={} email={}", user.getId(), user.getEmail());
                    return userMapper.toResponse(user);
                });
    }

    @Override
    public Mono<Void> deleteByToken(String token) {
        log.info("AuthServiceImpl::deleteByToken started");

        return this.accessTokenRepository.deleteByToken(token)
                .doOnSuccess(unused -> log.info("AuthServiceImpl::deleteByToken completed"));
    }

    @Override
    public Mono<AccessTokenDto> getAccessTokenByToken(String token) {
        log.debug("AuthServiceImpl::getAccessTokenByToken started");

        return this.accessTokenRepository.findByToken(token)
                .map(accessTokenMapper::toDto)
                .doOnNext(accessToken -> log.debug("AuthServiceImpl::getAccessTokenByToken completed accessTokenId={}",
                        accessToken.id()));
    }

    @Override
    public Mono<Boolean> existsAccessTokenByToken(String token) {
        log.debug("AuthServiceImpl::existsAccessTokenByToken started");

        return this.accessTokenRepository.existsByToken(token).flatMap(exists -> {
                if (!exists) {
                    log.warn("AuthServiceImpl::existsAccessTokenByToken rejected: token not found");
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid token"
                    ));
                }

                log.debug("AuthServiceImpl::existsAccessTokenByToken completed");
                return Mono.just(true);
            });
    }
}
