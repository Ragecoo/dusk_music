package io.github.ragecoo.dusk_music.dto.trackdto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackItem {

    TrackResponse track;
    Integer position;


}
