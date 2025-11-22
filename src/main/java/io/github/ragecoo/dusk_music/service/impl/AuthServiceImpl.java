package io.github.ragecoo.dusk_music.service.impl;


import io.github.ragecoo.dusk_music.dto.authdto.JwtAuthDto;
import io.github.ragecoo.dusk_music.dto.authdto.LoginRequest;
import io.github.ragecoo.dusk_music.dto.authdto.RefreshTokenDto;
import io.github.ragecoo.dusk_music.dto.authdto.RegisterRequest;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.exceptions.TakenException;
import io.github.ragecoo.dusk_music.model.RefreshToken;
import io.github.ragecoo.dusk_music.model.User;
import io.github.ragecoo.dusk_music.model.enums.Role;
import io.github.ragecoo.dusk_music.repository.RefreshTokenRepository;
import io.github.ragecoo.dusk_music.repository.UserRepository;
import io.github.ragecoo.dusk_music.security.jwt.JwtService;
import io.github.ragecoo.dusk_music.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import java.util.Optional;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;




    @Override @Transactional
    public JwtAuthDto register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new TakenException("This Email is already taken");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new TakenException("This username is already taken");
        }


        User u = new User();
        u.setEmail(request.getEmail().trim());
        u.setUsername(request.getUsername().trim());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setRole(Role.USER);
        u.setCreatedAt(LocalDateTime.now());
        u.setSubActive(false);
        u = userRepository.save(u);




        JwtAuthDto jwt = jwtService.generateAuthToken(u.getUsername());
        String access= jwt.getAccessToken();
        String refresh= jwt.getRefreshToken();



        refreshTokenRepository.deleteAllByUserId(u.getId());
        RefreshToken rt = new RefreshToken();
                rt.setUser(u);
                rt.setToken(refresh);
                rt.setExpiresAt(LocalDateTime.now().plusDays(30));

        refreshTokenRepository.save(rt);

        return jwt;

    }

    @Override
    @Transactional
    public JwtAuthDto login(LoginRequest request) {
        String id = request.getUsernameOrEmail().trim();
        String rawPassword = request.getPassword();

        // Находим пользователя по email или username
        Optional<User> byEmail = userRepository.findByEmailIgnoreCase(id);
        User u = byEmail.orElseGet(() ->
                userRepository.findByUsername(id)
                        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"))
        );

        // Проверяем пароль
        if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        JwtAuthDto jwt= jwtService.generateAuthToken(u.getUsername());
        String access = jwt.getAccessToken();
        String refresh = jwt.getRefreshToken();

        refreshTokenRepository.deleteAllByUserId(u.getId());
        RefreshToken rt = new RefreshToken();
        rt.setUser(u);
        rt.setToken(refresh);
        rt.setExpiresAt(LocalDateTime.now().plusDays(30));

        refreshTokenRepository.save(rt);

        return jwt;
    }

    @Override
    public JwtAuthDto refresh(RefreshTokenDto request) {
        String refresh = request.getRefreshToken();
        if (!jwtService.validateJwtToken(refresh)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        Long userId= jwtService.getUserIdFromToken(refresh);

        RefreshToken stored = refreshTokenRepository.findByToken(refresh)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(stored.getId());
            throw new BadCredentialsException("Refresh token expired");
        }

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        JwtAuthDto jwt= jwtService.generateAuthToken(u.getUsername());
        String newAccess = jwt.getAccessToken();
        String newRefresh = jwt.getRefreshToken();

        refreshTokenRepository.deleteAllByUserId(u.getId());
        RefreshToken rt = new RefreshToken();
        rt.setUser(u);
        rt.setToken(newRefresh);
        rt.setExpiresAt(LocalDateTime.now().plusDays(30));

        refreshTokenRepository.save(rt);

        return jwt;
    }


}
