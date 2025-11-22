package io.github.ragecoo.dusk_music.dto.trackdto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackCreateRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    private Long artistId;

    private Long albumId;
    private Long genreId;

    @NotNull
    private String audioUrl;

    @NotNull
    private String coverUrl;

    //если что вернусь
    @NotNull
    LocalDateTime releaseDate;


}
