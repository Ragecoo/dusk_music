package io.github.ragecoo.dusk_music.dto.profile;

import io.github.ragecoo.dusk_music.dto.userdto.UserRef;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String email;
    private String username;
    private String avatarUrl;
    private boolean subscriptionStatus;
    LocalDateTime createdAt;

}