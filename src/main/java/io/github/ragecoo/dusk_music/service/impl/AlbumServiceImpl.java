package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumCreateRequest;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumResponse;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.mapper.AlbumMapper;
import io.github.ragecoo.dusk_music.model.Album;
import io.github.ragecoo.dusk_music.model.Artist;
import io.github.ragecoo.dusk_music.model.Track;
import io.github.ragecoo.dusk_music.repository.AlbumRepository;
import io.github.ragecoo.dusk_music.repository.ArtistRepository;
import io.github.ragecoo.dusk_music.repository.TrackRepository;
import io.github.ragecoo.dusk_music.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final AlbumMapper albumMapper;

    @Override
    @Transactional
    public AlbumResponse create(AlbumCreateRequest request) {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        Album album = albumMapper.toEntity(request, artist);
        album = albumRepository.save(album);

        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumResponse get(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional
    public AlbumResponse update(Long albumId, AlbumUpdateRequest request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        if (request.getTitle() != null) {
            album.setTitle(request.getTitle());
        }
        if (request.getPhotoUrl() != null) {
            album.setPhotoUrl(request.getPhotoUrl());
        }
        if (request.getReleaseDate() != null) {
            album.setReleaseDate(request.getReleaseDate());
        }
        if (request.getArtistId() != null) {
            Artist artist = artistRepository.findById(request.getArtistId())
                    .orElseThrow(() -> new NotFoundException("Artist not found"));
            album.setArtist(artist);
        }

        album = albumRepository.save(album);
        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional
    public void delete(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new NotFoundException("Album not found");
        }
        albumRepository.deleteById(albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumResponse> listByArtist(Long artistId, Pageable pageable) {
        if (!artistRepository.existsById(artistId)) {
            throw new NotFoundException("Artist not found");
        }

        Page<Album> albums = albumRepository.findByArtistId(artistId, pageable);
        return albums.map(albumMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumResponse> listAll(Pageable pageable) {
        Page<Album> albums = albumRepository.findAllByOrderByReleaseDateDesc(pageable);
        return albums.map(albumMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackShortResponse> listTracks(Long albumId, Pageable pageable) {
        if (!albumRepository.existsById(albumId)) {
            throw new NotFoundException("Album not found");
        }

        Page<Track> tracks = trackRepository.findByAlbumId(albumId, pageable);
        return tracks.map(track -> {
            TrackShortResponse response = new TrackShortResponse();
            response.setId(track.getId());
            response.setTitle(track.getTitle());
            response.setDurationSec(track.getDuration());
            response.setPlayCount(track.getPlayCount());
            response.setAlbumTitle(track.getAlbum() != null ? track.getAlbum().getTitle() : null);
            response.setArtistName(track.getArtist() != null ? track.getArtist().getArtistName() : null);
            return response;
        });
    }
}

