package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.playlistdto.AddTrackToPlaylistRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistCreateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistResponse;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.RemoveTrackFromPlaylistRequest;

import java.util.List;

public interface PlaylistService {

    PlaylistResponse create(Long userId, PlaylistCreateRequest request);

    PlaylistResponse get(Long playlistId);

    PlaylistResponse update(Long userId, Long playlistId, PlaylistUpdateRequest request);

    void delete(Long userId, Long playlistId);

    List<PlaylistResponse> listByUser(Long userId);

    PlaylistResponse getFavorite(Long userId);

    void addTrack(Long userId, AddTrackToPlaylistRequest request);

    void removeTrack(Long userId, RemoveTrackFromPlaylistRequest request);
}

