package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackService {

    TrackResponse create(TrackCreateRequest request);

    TrackResponse get(Long trackId);

    TrackResponse update(Long trackId, TrackUpdateRequest request);

    void delete(Long trackId);

    Page<TrackShortResponse> listByArtist(Long artistId, Pageable pageable);

    Page<TrackShortResponse> listByAlbum(Long albumId, Pageable pageable);

    Page<TrackShortResponse> listByGenre(Long genreId, Pageable pageable);

    Page<TrackShortResponse> listAll(Pageable pageable);

    Page<TrackShortResponse> listPopular(Pageable pageable);

    Page<TrackShortResponse> listRecent(Pageable pageable);

    Page<TrackShortResponse> searchByTitle(String title, Pageable pageable);

    void incrementPlayCount(Long trackId);
}

