package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {

    ArtistResponse get(Long currentUserId, Long artistId);

    Page<TrackShortResponse> listTracks(Long artistId, Pageable pageable);

    void follow(Long userId, Long artistId);

    void unfollow(Long userId, Long artistId);

    boolean isFollowed(Long userId, Long artistId);

    Page<ArtistResponse> myFavorites(Long userId, Pageable pageable);

    long followersCount(Long artistId);
}
