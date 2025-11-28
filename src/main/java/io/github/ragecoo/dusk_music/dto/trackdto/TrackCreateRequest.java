package io.github.ragecoo.dusk_music.dto.trackdto;

import jakarta.validation.constraints.*;
import lombok.*;

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

    // Опционально: если не указано, будет использована обложка альбома
    private String coverUrl;

    //если что вернусь
    @NotNull
    LocalDateTime releaseDate;


}
