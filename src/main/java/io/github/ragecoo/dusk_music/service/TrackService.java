package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrackService {

    TrackResponse create(TrackCreateRequest request);

    TrackResponse get(Long trackId);

    TrackResponse update(Long trackId, TrackUpdateRequest request);

    void delete(Long trackId);

    Page<TrackResponse> listByArtist(Long artistId, Pageable pageable);

    Page<TrackResponse> listByAlbum(Long albumId, Pageable pageable);

    Page<TrackResponse> listByGenre(Long genreId, Pageable pageable);

    Page<TrackResponse> listAll(Pageable pageable);

    Page<TrackResponse> listPopular(Pageable pageable);

    Page<TrackResponse> listRecent(Pageable pageable);

    Page<TrackResponse> searchByTitle(String title, Pageable pageable);

    void incrementPlayCount(Long trackId);
}

