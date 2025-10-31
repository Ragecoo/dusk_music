package io.github.ragecoo.dusk_music.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Смена email")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChangeEmailRequest {

    @NotNull @Email @Size(max = 100)
    @Schema(example = "new@example.com")
    private String newEmail;

    @NotNull
    @Schema(description = "Подтверждение паролем для безопасности", example = "YourP@ssw0rd")
    private String currentPassword;
}