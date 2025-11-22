package io.github.ragecoo.dusk_music.dto.playlistdto;

import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistTrackItem {
        TrackResponse track;
        Integer position;
}
