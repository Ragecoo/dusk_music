package io.github.ragecoo.dusk_music.dto.playlistdto;

import io.github.ragecoo.dusk_music.dto.userdto.UserRef;

import java.time.LocalDateTime;
import java.util.List;

public class PlaylistResponse
{
        Long id;
        UserRef owner;
        String name;
        boolean isFavorite;
        List<PlaylistTrackItem> track;
        LocalDateTime createdAt;
}
