package io.github.ragecoo.dusk_music.dto.playlistdto;

import io.github.ragecoo.dusk_music.dto.userdto.UserRef;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlaylistResponse {
        Long id;
        UserRef owner;
        String name;
        boolean isFavorite;
        List<PlaylistTrackItem> track;
        LocalDateTime createdAt;
}
