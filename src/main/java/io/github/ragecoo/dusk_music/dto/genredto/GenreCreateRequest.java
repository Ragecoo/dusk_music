package io.github.ragecoo.dusk_music.dto.genredto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreCreateRequest {

    @Size(max = 100)
    private String title;

}
