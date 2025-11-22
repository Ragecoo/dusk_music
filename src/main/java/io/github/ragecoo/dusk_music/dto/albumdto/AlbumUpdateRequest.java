package io.github.ragecoo.dusk_music.dto.albumdto;

import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumUpdateRequest {

    @Size(min = 1, max = 50)
    private String title;
    private String photoUrl;
    private LocalDateTime releaseDate;
    private Long artistId;
}
