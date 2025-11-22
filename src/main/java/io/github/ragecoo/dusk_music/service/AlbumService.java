package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumCreateRequest;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumResponse;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumService {

    AlbumResponse create(AlbumCreateRequest request);

    AlbumResponse get(Long albumId);

    AlbumResponse update(Long albumId, AlbumUpdateRequest request);

    void delete(Long albumId);

    Page<AlbumResponse> listByArtist(Long artistId, Pageable pageable);

    Page<AlbumResponse> listAll(Pageable pageable);

    Page<TrackShortResponse> listTracks(Long albumId, Pageable pageable);
}

