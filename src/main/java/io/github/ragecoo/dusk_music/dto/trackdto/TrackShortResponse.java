package io.github.ragecoo.dusk_music.dto.trackdto;

import lombok.Data;

@Data
public class TrackShortResponse {
    private Long id;
    private String title;
    private Integer durationSec;
    private Long playCount;
    private String albumTitle;
    private String artistName;
}
