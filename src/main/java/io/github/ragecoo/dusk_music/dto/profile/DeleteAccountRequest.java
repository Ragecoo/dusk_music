package io.github.ragecoo.dusk_music.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Удаление аккаунта (подтверждение паролем)")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeleteAccountRequest {
    @NotNull
    @Schema(example = "YourP@ssw0rd")
    private String password;
}