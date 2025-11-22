package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.authdto.JwtAuthDto;
import io.github.ragecoo.dusk_music.dto.authdto.LoginRequest;
import io.github.ragecoo.dusk_music.dto.authdto.RefreshTokenDto;
import io.github.ragecoo.dusk_music.dto.authdto.RegisterRequest;

public interface AuthService {

    JwtAuthDto register(RegisterRequest request);
    JwtAuthDto login(LoginRequest request);
    JwtAuthDto refresh(RefreshTokenDto request);
}
