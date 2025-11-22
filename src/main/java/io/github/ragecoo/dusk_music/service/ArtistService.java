package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistCreateRequest;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {

    ArtistResponse create(ArtistCreateRequest request);

    ArtistResponse get(Long currentUserId, Long artistId);

    ArtistResponse update(Long artistId, ArtistUpdateRequest request);

    void delete(Long artistId);

    Page<ArtistResponse> listAll(Pageable pageable);

    Page<ArtistResponse> search(String query, Pageable pageable);

    Page<TrackShortResponse> listTracks(Long artistId, Pageable pageable);

    void follow(Long userId, Long artistId);

    void unfollow(Long userId, Long artistId);

    boolean isFollowed(Long userId, Long artistId);

    Page<ArtistResponse> myFavorites(Long userId, Pageable pageable);

    long followersCount(Long artistId);
}
