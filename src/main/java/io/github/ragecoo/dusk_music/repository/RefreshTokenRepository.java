package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteAllByUserId(Long userId);
}