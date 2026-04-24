package io.yerektus.qadam.coreapi.modules.auth.service;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.*;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.AccessToken;
import io.yerektus.qadam.coreapi.modules.auth.model.entity.User;
import io.yerektus.qadam.coreapi.modules.auth.model.mapper.AccessTokenMapper;
import io.yerektus.qadam.coreapi.modules.auth.model.mapper.UserMapper;
import io.yerektus.qadam.coreapi.modules.auth.repository.AccessTokenRepository;
import io.yerektus.qadam.coreapi.modules.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AccessTokenMapper accessTokenMapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper, AccessTokenRepository accessTokenRepository, AccessTokenMapper accessTokenMapper) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.accessTokenMapper = accessTokenMapper;
    }

    public Mono<RegisterResponse> register(RegisterBody payload) {
        return userRepository.existsByEmail(payload.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered"
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

                    return accessTokenRepository.save(accessToken).map(savedAccessToken -> new RegisterResponse(userMapper.toDto(savedUser), accessTokenMapper.toDto(accessToken)));
                });
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                )))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password"
                        ));
                    }

                    String token = jwtService.generateToken(user.getId(), user.getRole());
                    return Mono.just(new LoginResponse(token, user.getId(), user.getEmail(), user.getRole()));
                });
    }

    public Mono<UserDto> getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                )))
                .map(userMapper::toDto);
    }
}
