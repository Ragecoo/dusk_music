package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.profile.*;

public interface ProfileService {

    ProfileResponse me(Long userId);

    ProfileResponse updateProfile(Long userId, UpdateProfileRequest req);

    void changeEmail(Long userId, ChangeEmailRequest req);

    void changePassword(Long userId, ChangePasswordRequest req);

    void logoutAllDevices(Long userId);

    void deleteAccount(Long userId, DeleteAccountRequest req);


}
