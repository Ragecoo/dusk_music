package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.authdto.JwtAuthDto;
import io.github.ragecoo.dusk_music.dto.authdto.LoginRequest;
import io.github.ragecoo.dusk_music.dto.authdto.RefreshTokenDto;
import io.github.ragecoo.dusk_music.dto.authdto.RegisterRequest;
import io.github.ragecoo.dusk_music.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Контроллер для работы с аутентификацией")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<JwtAuthDto> register(@Valid @RequestBody RegisterRequest request) {
        JwtAuthDto tokens = authService.register(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя")
    public ResponseEntity<JwtAuthDto> login(@Valid @RequestBody LoginRequest request) {
        JwtAuthDto tokens = authService.login(request);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access токена по refresh токену")
    public ResponseEntity<JwtAuthDto> refresh(@Valid @RequestBody RefreshTokenDto request) {
        JwtAuthDto tokens = authService.refresh(request);
        return ResponseEntity.ok(tokens);
    }
}

