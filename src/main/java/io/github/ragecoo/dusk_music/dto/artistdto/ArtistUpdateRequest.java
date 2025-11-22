package io.github.ragecoo.dusk_music.dto.artistdto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistUpdateRequest {

    @Size(min = 1, max = 50)
    private String name;
    @Size(max = 255)
    private String photoUrl;
    @Size(max = 500)
    private String bio;
}
