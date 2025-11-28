package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.profile.*;
import io.github.ragecoo.dusk_music.dto.userdto.AuthUser;
import io.github.ragecoo.dusk_music.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Контроллер для работы с профилем пользователя")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Получить текущий профиль пользователя")
    public ResponseEntity<ProfileResponse> me() {
        Long userId = getCurrentUserId();
        ProfileResponse response = profileService.me(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Обновить профиль пользователя")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = getCurrentUserId();
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/email")
    @Operation(summary = "Изменить email пользователя")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        Long userId = getCurrentUserId();
        profileService.changeEmail(userId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @Operation(summary = "Изменить пароль пользователя")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = getCurrentUserId();
        profileService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/avatar")
    @Operation(summary = "Загрузить аватар пользователя")
    public ResponseEntity<ProfileResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId();
        ProfileResponse response = profileService.uploadAvatar(userId, file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Выйти из всех устройств")
    public ResponseEntity<Void> logoutAllDevices() {
        Long userId = getCurrentUserId();
        profileService.logoutAllDevices(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "Удалить аккаунт пользователя")
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        Long userId = getCurrentUserId();
        profileService.deleteAccount(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает ID текущего аутентифицированного пользователя из SecurityContext
     * @return ID пользователя
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser authUser) {
            return authUser.id();
        }
        return null;
    }
}

