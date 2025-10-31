package io.github.ragecoo.dusk_music.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Смена пароля")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChangePasswordRequest {

    @NotNull
    @Schema(example = "OldP@ssw0rd")
    private String currentPassword;

    @NotNull @Size(min = 8, max = 64)
    @Schema(example = "NewStrongerP@ss1")
    private String newPassword;

    @NotNull @Size(min = 8, max = 64)
    @Schema(example = "NewStrongerP@ss1")
    private String confirmNewPassword;
}