package io.github.ragecoo.dusk_music.dto.trackdto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackUpdateRequest {
    @Size(min = 1, max = 50)
    private String title;

    private Long artistId;
    private Long albumId;
    private Long genreId;

    private String audioUrl;
    private String coverUrl;
    private LocalDateTime releaseDate;
}