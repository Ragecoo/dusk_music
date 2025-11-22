package io.github.ragecoo.dusk_music.dto.albumdto;

import java.time.LocalDateTime;

public record AlbumResponse(

        Long id,

        String title,

        String photoUrl,

        LocalDateTime releaseDate,

        Long artistId) {
}
