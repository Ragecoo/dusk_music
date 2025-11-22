package io.github.ragecoo.dusk_music.dto.trackdto;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;


import java.time.LocalDateTime;

public record TrackResponse(
        Long id,
        String title,
        Integer duration,
        String audioUrl,
        String coverUrl,
        Long playCount,
        LocalDateTime releaseDate,
        ArtistRef artist,
        AlbumRef album,
        GenreRef genre
) {}