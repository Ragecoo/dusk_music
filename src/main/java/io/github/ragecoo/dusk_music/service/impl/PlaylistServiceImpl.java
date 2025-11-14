package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;
import io.github.ragecoo.dusk_music.dto.playlistdto.AddTrackToPlaylistRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistCreateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistResponse;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistTrackItem;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.RemoveTrackFromPlaylistRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.userdto.UserRef;
import io.github.ragecoo.dusk_music.exceptions.ForbiddenException;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.exceptions.TakenException;
import io.github.ragecoo.dusk_music.mapper.AlbumMapper;
import io.github.ragecoo.dusk_music.mapper.ArtistMapper;
import io.github.ragecoo.dusk_music.mapper.GenreMapper;
import io.github.ragecoo.dusk_music.mapper.PlaylistMapper;
import io.github.ragecoo.dusk_music.mapper.TrackMapper;
import io.github.ragecoo.dusk_music.mapper.UserMapper;
import io.github.ragecoo.dusk_music.model.PlayListTrack;
import io.github.ragecoo.dusk_music.model.Playlist;
import io.github.ragecoo.dusk_music.model.Track;
import io.github.ragecoo.dusk_music.model.User;
import io.github.ragecoo.dusk_music.repository.PlayListTrackRepository;
import io.github.ragecoo.dusk_music.repository.PlaylistRepository;
import io.github.ragecoo.dusk_music.repository.TrackRepository;
import io.github.ragecoo.dusk_music.repository.UserRepository;
import io.github.ragecoo.dusk_music.service.PlaylistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final PlayListTrackRepository playListTrackRepository;
    private final PlaylistMapper playlistMapper;
    private final UserMapper userMapper;
    private final TrackMapper trackMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final GenreMapper genreMapper;

    @Override
    @Transactional
    public PlaylistResponse create(Long userId, PlaylistCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Проверка на уникальность имени плейлиста для пользователя
        if (playlistRepository.existsByUserIdAndNameIgnoreCase(userId, request.getName())) {
            throw new TakenException("Playlist with this name already exists");
        }

        Playlist playlist = playlistMapper.toEntity(request, user);
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setFavorite(false);
        playlist = playlistRepository.save(playlist);

        return toResponse(playlist);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse get(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        return toResponse(playlist);
    }

    @Override
    @Transactional
    public PlaylistResponse update(Long userId, Long playlistId, PlaylistUpdateRequest request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // Проверка прав доступа
        if (!playlist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to update this playlist");
        }

        if (request.getName() != null) {
            // Проверка на уникальность имени (кроме текущего плейлиста)
            if (playlistRepository.existsByUserIdAndNameIgnoreCase(userId, request.getName()) 
                    && !playlist.getName().equalsIgnoreCase(request.getName())) {
                throw new TakenException("Playlist with this name already exists");
            }
            playlist.setName(request.getName());
        }

        playlist = playlistRepository.save(playlist);
        return toResponse(playlist);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // Проверка прав доступа
        if (!playlist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to delete this playlist");
        }

        playlistRepository.deleteById(playlistId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponse> listByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Playlist> playlists = playlistRepository.findByUserId(userId);
        return playlists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse getFavorite(Long userId) {
        Playlist favorite = playlistRepository.findByUserIdAndIsFavoriteTrue(userId)
                .orElseThrow(() -> new NotFoundException("Favorite playlist not found"));

        return toResponse(favorite);
    }

    @Override
    @Transactional
    public void addTrack(Long userId, AddTrackToPlaylistRequest request) {
        Playlist playlist = playlistRepository.findById(request.getPlaylistId())
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // Проверка прав доступа
        if (!playlist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to modify this playlist");
        }

        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new NotFoundException("Track not found"));

        // Проверка, не добавлен ли трек уже
        if (playListTrackRepository.existsByPlaylistIdAndTrackId(request.getPlaylistId(), request.getTrackId())) {
            throw new TakenException("Track is already in the playlist");
        }

        // Определяем позицию (последняя позиция + 1)
        List<PlayListTrack> existingTracks = playListTrackRepository.findByPlaylistIdOrderByPositionAsc(request.getPlaylistId());
        int nextPosition = existingTracks.isEmpty() ? 1 : existingTracks.get(existingTracks.size() - 1).getPosition() + 1;

        PlayListTrack playListTrack = new PlayListTrack();
        playListTrack.setPlaylist(playlist);
        playListTrack.setTrack(track);
        playListTrack.setPosition(nextPosition);
        playListTrackRepository.save(playListTrack);
    }

    @Override
    @Transactional
    public void removeTrack(Long userId, RemoveTrackFromPlaylistRequest request) {
        Playlist playlist = playlistRepository.findById(request.getPlaylistId())
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // Проверка прав доступа
        if (!playlist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to modify this playlist");
        }

        if (!trackRepository.existsById(request.getTrackId())) {
            throw new NotFoundException("Track not found");
        }

        if (!playListTrackRepository.existsByPlaylistIdAndTrackId(request.getPlaylistId(), request.getTrackId())) {
            throw new NotFoundException("Track is not in the playlist");
        }

        playListTrackRepository.deleteByPlaylistIdAndTrackId(request.getPlaylistId(), request.getTrackId());
        
        // Обновляем позиции оставшихся треков
        reorderPositions(request.getPlaylistId());
    }

    /**
     * Переупорядочивает позиции треков в плейлисте после удаления
     */
    private void reorderPositions(Long playlistId) {
        List<PlayListTrack> tracks = playListTrackRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        for (int i = 0; i < tracks.size(); i++) {
            tracks.get(i).setPosition(i + 1);
            playListTrackRepository.save(tracks.get(i));
        }
    }

    /**
     * Преобразует Playlist в PlaylistResponse
     */
    private PlaylistResponse toResponse(Playlist playlist) {
        PlaylistResponse response = new PlaylistResponse();
        response.setId(playlist.getId());
        response.setName(playlist.getName());
        response.setFavorite(playlist.isFavorite());
        response.setCreatedAt(playlist.getCreatedAt());
        
        // Преобразуем владельца
        UserRef ownerRef = userMapper.toRef(playlist.getUser());
        response.setOwner(ownerRef);

        // Получаем треки плейлиста
        List<PlayListTrack> playListTracks = playListTrackRepository.findByPlaylistIdOrderByPositionAsc(playlist.getId());
        List<PlaylistTrackItem> trackItems = playListTracks.stream()
                .map(plt -> {
                    PlaylistTrackItem item = new PlaylistTrackItem();
                    item.setTrack(toTrackResponse(plt.getTrack()));
                    item.setPosition(plt.getPosition());
                    return item;
                })
                .collect(Collectors.toList());
        response.setTrack(trackItems);

        return response;
    }

    /**
     * Преобразует Track в TrackResponse
     */
    private TrackResponse toTrackResponse(Track track) {
        ArtistRef artistRef = track.getArtist() != null 
                ? artistMapper.toRef(track.getArtist()) 
                : null;
        AlbumRef albumRef = track.getAlbum() != null 
                ? albumMapper.toRef(track.getAlbum()) 
                : null;
        GenreRef genreRef = track.getGenre() != null 
                ? genreMapper.toRef(track.getGenre()) 
                : null;

        return trackMapper.toResponse(track, artistRef, albumRef, genreRef);
    }
}

