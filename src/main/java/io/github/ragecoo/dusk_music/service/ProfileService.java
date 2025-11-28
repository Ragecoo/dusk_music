package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.profile.*;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse me(Long userId);

    ProfileResponse updateProfile(Long userId, UpdateProfileRequest req);

    ProfileResponse uploadAvatar(Long userId, MultipartFile file);

    void changeEmail(Long userId, ChangeEmailRequest req);

    void changePassword(Long userId, ChangePasswordRequest req);

    void logoutAllDevices(Long userId);

    void deleteAccount(Long userId, DeleteAccountRequest req);


}
