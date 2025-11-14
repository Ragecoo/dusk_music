package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
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
    public Page<TrackShortResponse> listByArtist(Long artistId, Pageable pageable) {
        if (!artistRepository.existsById(artistId)) {
            throw new NotFoundException("Artist not found");
        }

        Page<Track> tracks = trackRepository.findByArtistId(artistId, pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listByAlbum(Long albumId, Pageable pageable) {
        if (!albumRepository.existsById(albumId)) {
            throw new NotFoundException("Album not found");
        }

        Page<Track> tracks = trackRepository.findByAlbumId(albumId, pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listByGenre(Long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new NotFoundException("Genre not found");
        }

        Page<Track> tracks = trackRepository.findByGenreId(genreId, pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listAll(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAll(pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listPopular(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAllByOrderByPlayCountDesc(pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listRecent(Pageable pageable) {
        Page<Track> tracks = trackRepository.findAllByOrderByReleaseDateDesc(pageable);
        return tracks.map(this::toShortResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> searchByTitle(String title, Pageable pageable) {
        Page<Track> tracks = trackRepository.findByTitleContainingIgnoreCase(title, pageable);
        return tracks.map(this::toShortResponse);
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

        return trackMapper.toResponse(track, artistRef, albumRef, genreRef);
    }

    private TrackShortResponse toShortResponse(Track track) {
        TrackShortResponse response = new TrackShortResponse();
        response.setId(track.getId());
        response.setTitle(track.getTitle());
        response.setDurationSec(track.getDuration());
        response.setPlayCount(track.getPlayCount());
        response.setAlbumTitle(track.getAlbum() != null ? track.getAlbum().getTitle() : null);
        response.setArtistName(track.getArtist() != null ? track.getArtist().getArtistName() : null);
        return response;
    }
}

