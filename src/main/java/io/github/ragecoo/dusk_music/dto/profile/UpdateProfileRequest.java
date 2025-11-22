package io.github.ragecoo.dusk_music.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Обновление профиля: аватар и, опционально, username")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateProfileRequest {

    @Size(min = 3, max = 50)
    @Schema(description = "Новый ник. Если null — не меняем")
    private String username;

    @Size(max = 255)
    @Schema(description = "URL аватарки. Если null — не меняем")
    private String avatarUrl;
}