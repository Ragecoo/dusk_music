package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.profile.*;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.exceptions.TakenException;
import io.github.ragecoo.dusk_music.mapper.UserMapper;
import io.github.ragecoo.dusk_music.model.User;
import io.github.ragecoo.dusk_music.repository.RefreshTokenRepository;
import io.github.ragecoo.dusk_music.repository.UserRepository;
import io.github.ragecoo.dusk_music.service.ProfileService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ProfileResponse me(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ProfileResponse response= new ProfileResponse();
        response.setId(u.getId());
        response.setUsername(u.getUsername());
        response.setEmail(u.getEmail());
        response.setAvatarUrl(u.getAvatarUrl());
        response.setSubscriptionStatus(u.isSubActive());
        response.setCreatedAt(u.getCreatedAt());

        return response;
    }

    @Override @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean changed = false;

        if (StringUtils.hasText(req.getUsername())) {
            String newUsername = req.getUsername().trim();
            if (!newUsername.equals(u.getUsername())
                    && userRepository.existsByUsername(newUsername)) {
                throw new TakenException("Username is already taken");
            }
            u.setUsername(newUsername);
            changed = true;
        }

        if (req.getAvatarUrl() != null) {
            u.setAvatarUrl(req.getAvatarUrl().trim());
            changed = true;
        }




        if (changed) {
            userRepository.save(u);
        }
        ProfileResponse response= new ProfileResponse();
        response.setId(u.getId());
        response.setUsername(u.getUsername());
        response.setEmail(u.getEmail());
        response.setAvatarUrl(u.getAvatarUrl());
        response.setSubscriptionStatus(u.isSubActive());
        response.setCreatedAt(u.getCreatedAt());

        return response;
    }

    @Override
    @Transactional
    public void changeEmail(Long userId, ChangeEmailRequest req) {
        if (!StringUtils.hasText(req.getNewEmail())) {
            throw new IllegalArgumentException("New email is required");
        }
        if (!StringUtils.hasText(req.getCurrentPassword())) {
            throw new IllegalArgumentException("Password is required");
        }

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String newEmail = req.getNewEmail().trim();
        if (userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new TakenException("Email is already taken");
        }

        u.setEmail(newEmail);
        userRepository.save(u);

    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        if (!StringUtils.hasText(req.getCurrentPassword())
                || !StringUtils.hasText(req.getNewPassword())
                || !StringUtils.hasText(req.getConfirmNewPassword())) {
            throw new IllegalArgumentException("All password fields are required");
        }
        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);

        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    public void logoutAllDevices(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);

    }

    @Override
    public void deleteAccount(Long userId, DeleteAccountRequest req) {

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!StringUtils.hasText(req.getPassword())
                || !passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        refreshTokenRepository.deleteAllByUserId(userId);

        userRepository.deleteById(userId);

    }
}
