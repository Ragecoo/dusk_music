package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackUpdateRequest;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.mapper.AlbumMapper;
import io.github.ragecoo.dusk_music.mapper.ArtistMapper;
import io.github.ragecoo.dusk_music.mapper.GenreMapper;
import io.github.ragecoo.dusk_music.mapper.TrackMapper;
import io.github.ragecoo.dusk_music.model.Album;
import io.github.ragecoo.dusk_music.model.Artist;
import io.github.ragecoo.dusk_music.model.Genre;
import io.github.ragecoo.dusk_music.model.Track;
import io.github.ragecoo.dusk_music.repository.AlbumRepository;
import io.github.ragecoo.dusk_music.repository.ArtistRepository;
import io.github.ragecoo.dusk_music.repository.GenreRepository;
import io.github.ragecoo.dusk_music.repository.TrackRepository;
import io.github.ragecoo.dusk_music.service.TrackService;
import io.github.ragecoo.dusk_music.util.AudioDurationExtractor;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final TrackMapper trackMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final GenreMapper genreMapper;
    private final AudioDurationExtractor audioDurationExtractor;

    @Override
    @Transactional
    public TrackResponse create(TrackCreateRequest request) {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        Album album = null;
        if (request.getAlbumId() != null) {
            album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found"));
        } else {
            throw new NotFoundException("Album is required");
        }

        Genre genre = null;
        if (request.getGenreId() != null) {
            genre = genreRepository.findById(request.getGenreId())
                    .orElse(null);
        }

        Track track = trackMapper.toEntity(request, album, artist, genre);
        
        // Автоматически устанавливаем обложку трека из альбома, если она не указана в запросе
        if (track.getCoverUrl() == null || track.getCoverUrl().isEmpty()) {
            // Используем обложку альбома, если она есть
            if (album.getPhotoUrl() != null && !album.getPhotoUrl().isEmpty()) {
                track.setCoverUrl(album.getPhotoUrl());
            } else {
                // Если у альбома нет обложки, нужно указать её в запросе
                throw new IllegalArgumentException("Cover URL is required. Either provide coverUrl in request or ensure album has a photoUrl");
            }
        }
        
        // Извлекаем длительность из MP3 файла
        Integer duration = audioDurationExtractor.extractDuration(request.getAudioUrl());
        if (duration != null && duration > 0) {
            track.setDuration(duration);
        } else {
            track.setDuration(0); // Fallback если не удалось извлечь длительность
        }
        
        track = trackRepository.save(track);

        return toResponse(track);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackResponse get(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found"));

        return toResponse(track);
    }

    @Override
    @Transactional
    public TrackResponse update(Long trackId, TrackUpdateRequest request) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found"));

        if (request.getTitle() != null) {
            track.setTitle(request.getTitle());
        }
        if (request.getAudioUrl() != null) {
            track.setAudioUrl(request.getAudioUrl());
            // Если изменился аудиофайл, обновляем длительность
            Integer duration = audioDurationExtractor.extractDuration(request.getAudioUrl());
            if (duration != null && duration > 0) {
                track.setDuration(duration);
            }
        }
        if (request.getCoverUrl() != null) {
            track.setCoverUrl(request.getCoverUrl());
        }
        if (request.getReleaseDate() != null) {
            track.setReleaseDate(request.getReleaseDate());
        }
        if (request.getArtistId() != null) {
            Artist artist = artistRepository.findById(request.getArtistId())
                    .orElseThrow(() -> new NotFoundException("Artist not found"));
            track.setArtist(artist);
        }
        if (request.getAlbumId() != null) {
            Album album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found"));
            track.setAlbum(album);
            // При изменении альбома автоматически обновляем обложку трека из альбома
            if (album.getPhotoUrl() != null && !album.getPhotoUrl().isEmpty()) {
                track.setCoverUrl(album.getPhotoUrl());
            }
        }
        if (request.getGenreId() != null) {
            Genre genre = genreRepository.findById(request.getGenreId())
                    .orElse(null);
            track.setGenre(genre);
        }

        track = trackRepository.save(track);
        return toResponse(track);
    }

    @Override
    @Transactional
    public void delete(Long trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new NotFoundException("Track not found");
        }
        trackRepository.deleteById(trackId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listByArtist(Long artistId, Pageable pageable) {
        if (!artistRepository.existsById(artistId)) {
            throw new NotFoundException("Artist not found");
        }

        Page<Track> tracks = trackRepository.findByArtistId(artistId, pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listByAlbum(Long albumId, Pageable pageable) {
        if (!albumRepository.existsById(albumId)) {
            throw new NotFoundException("Album not found");
        }

        Page<Track> tracks = trackRepository.findByAlbumId(albumId, pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listByGenre(Long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new NotFoundException("Genre not found");
        }

        Page<Track> tracks = trackRepository.findByGenreId(genreId, pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listAll(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAll(pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listPopular(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAllByOrderByPlayCountDesc(pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> listRecent(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAllByOrderByReleaseDateDesc(pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackResponse> searchByTitle(String title, Pageable pageable) {
        Page<Track> tracks = trackRepository.findByTitleContainingIgnoreCase(title, pageable);
        return tracks.map(this::toResponse);
    }

    @Override
    @Transactional
    public void incrementPlayCount(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found"));

        Long currentCount = track.getPlayCount() != null ? track.getPlayCount() : 0L;
        track.setPlayCount(currentCount + 1);
        trackRepository.save(track);
    }

    private TrackResponse toResponse(Track track) {
        ArtistRef artistRef = track.getArtist() != null 
                ? artistMapper.toRef(track.getArtist()) 
                : null;
        AlbumRef albumRef = track.getAlbum() != null 
                ? albumMapper.toRef(track.getAlbum()) 
                : null;
        GenreRef genreRef = track.getGenre() != null 
                ? genreMapper.toRef(track.getGenre()) 
                : null;

        // Преобразуем локальные пути в URL для API
        String audioUrl = convertToApiUrl(track.getAudioUrl());
        String coverUrl = convertToApiUrl(track.getCoverUrl());

        return new TrackResponse(
                track.getId(),
                track.getTitle(),
                track.getDuration(),
                audioUrl,
                coverUrl,
                track.getPlayCount(),
                track.getReleaseDate(),
                artistRef,
                albumRef,
                genreRef
        );
    }

    /**
     * Преобразует локальный путь в URL для API
     * Если путь уже является HTTP/HTTPS URL, возвращает как есть
     * Если путь начинается с audio/ или covers/, преобразует в /api/v1/files/...
     */
    private String convertToApiUrl(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        
        // Если уже HTTP/HTTPS URL, возвращаем как есть
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        
        // Если путь начинается с audio/ или covers/, преобразуем в URL API
        if (path.startsWith("audio/")) {
            return "/api/v1/files/" + path;
        }
        
        if (path.startsWith("covers/")) {
            return "/api/v1/files/" + path;
        }
        
        // Если это локальный путь без префикса, пытаемся определить тип
        // Для обратной совместимости возвращаем как есть
        return path;
    }

}

