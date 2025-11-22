package io.github.ragecoo.dusk_music.dto.genredto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreUpdateRequest {

    @Size(max = 100)
    String title;
}
