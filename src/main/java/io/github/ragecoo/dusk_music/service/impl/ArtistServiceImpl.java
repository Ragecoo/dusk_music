package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistCreateRequest;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.exceptions.TakenException;
import io.github.ragecoo.dusk_music.mapper.ArtistMapper;
import io.github.ragecoo.dusk_music.model.Artist;
import io.github.ragecoo.dusk_music.model.Track;
import io.github.ragecoo.dusk_music.model.User;
import io.github.ragecoo.dusk_music.model.UserArtistsFavorites;
import io.github.ragecoo.dusk_music.repository.ArtistRepository;
import io.github.ragecoo.dusk_music.repository.TrackRepository;
import io.github.ragecoo.dusk_music.repository.UserArtistsFavoritesRepository;
import io.github.ragecoo.dusk_music.repository.UserRepository;
import io.github.ragecoo.dusk_music.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final UserArtistsFavoritesRepository favoritesRepository;
    private final ArtistMapper artistMapper;

    @Override
    @Transactional
    public ArtistResponse create(ArtistCreateRequest request) {
        // Проверка на уникальность имени артиста
        if (artistRepository.findByArtistNameContainingIgnoreCase(request.getName().trim(), Pageable.unpaged())
                .stream()
                .anyMatch(a -> a.getArtistName().equalsIgnoreCase(request.getName().trim()))) {
            throw new TakenException("Artist with this name already exists");
        }

        Artist artist = artistMapper.toEntity(request);
        if (artist.getArtistName() != null) {
            artist.setArtistName(artist.getArtistName().trim());
        }
        artist = artistRepository.save(artist);

        return toResponse(artist, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistResponse get(Long currentUserId, Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));
        
        return toResponse(artist, currentUserId);
    }

    @Override
    @Transactional
    public ArtistResponse update(Long artistId, ArtistUpdateRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            String newName = request.getName().trim();
            // Проверка на уникальность имени (кроме текущего артиста)
            artistRepository.findByArtistNameContainingIgnoreCase(newName, Pageable.unpaged())
                    .stream()
                    .filter(a -> !a.getId().equals(artistId))
                    .filter(a -> a.getArtistName().equalsIgnoreCase(newName))
                    .findFirst()
                    .ifPresent(a -> {
                        throw new TakenException("Artist with this name already exists");
                    });
            artist.setArtistName(newName);
        }
        if (request.getPhotoUrl() != null) {
            artist.setPhotoUrl(request.getPhotoUrl());
        }
        if (request.getBio() != null) {
            artist.setBio(request.getBio());
        }

        artist = artistRepository.save(artist);
        return toResponse(artist, null);
    }

    @Override
    @Transactional
    public void delete(Long artistId) {
        if (!artistRepository.existsById(artistId)) {
            throw new NotFoundException("Artist not found");
        }
        artistRepository.deleteById(artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistResponse> listAll(Pageable pageable) {
        Page<Artist> artists = artistRepository.findAll(pageable);
        return artists.map(artist -> toResponse(artist, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistResponse> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }

        Page<Artist> artists = artistRepository.findByArtistNameContainingIgnoreCase(query.trim(), pageable);
        return artists.map(artist -> toResponse(artist, null));
    }

    @Override
    @Transactional
    public Page<TrackShortResponse> listTracks(Long artistId, Pageable pageable) {
        Page<Track> page = trackRepository.findByArtistId(artistId, pageable);
        return page.map(t -> {
            TrackShortResponse r = new TrackShortResponse();
            r.setId(t.getId());
            r.setTitle(t.getTitle());
            r.setDurationSec(t.getDuration());
            r.setPlayCount(t.getPlayCount());
            r.setAlbumTitle(t.getAlbum() != null ? t.getAlbum().getTitle() : null);
            r.setArtistName(t.getArtist() != null ? t.getArtist().getArtistName() : null);
            return r;
        });
    }

    @Transactional
    @Override
    public void follow(Long userId, Long artistId) {
        if (favoritesRepository.existsByUserIdAndArtistId(userId, artistId)) return;

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Artist a = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));

        UserArtistsFavorites fav = new UserArtistsFavorites();
        fav.setUser(u);
        fav.setArtist(a);
        fav.setCreatedAt(LocalDateTime.now());
        favoritesRepository.save(fav);

    }

    @Override
    @Transactional
    public void unfollow(Long userId, Long artistId) {

        favoritesRepository.findByUserIdAndArtistId(userId, artistId)
                .ifPresent(favoritesRepository::delete);

    }

    @Override
    @Transactional
    public boolean isFollowed(Long userId, Long artistId) {
        return favoritesRepository.existsByUserIdAndArtistId(userId, artistId);
    }

    @Override
    public Page<ArtistResponse> myFavorites(Long userId, Pageable pageable) {
        Page<UserArtistsFavorites> page = favoritesRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);


        return page.map(f -> {
            Artist artist = f.getArtist();
            ArtistResponse response = new ArtistResponse();
            response.setId(artist.getId());
            response.setName(artist.getArtistName());
            response.setPhotoUrl(artist.getPhotoUrl());
            response.setBio(artist.getBio());
            response.setFollowers(favoritesRepository.countByArtistId(artist.getId()));
            response.setFollowed(true);
            return response;
        });

    }

    @Override
    @Transactional
    public long followersCount(Long artistId) {
        return favoritesRepository.countByArtistId(artistId);
    }

    /**
     * Преобразует Artist в ArtistResponse
     */
    private ArtistResponse toResponse(Artist artist, Long currentUserId) {
        long followers = favoritesRepository.countByArtistId(artist.getId());
        boolean followed = currentUserId != null && favoritesRepository.existsByUserIdAndArtistId(currentUserId, artist.getId());

        ArtistResponse response = new ArtistResponse();
        response.setId(artist.getId());
        response.setName(artist.getArtistName());
        response.setPhotoUrl(artist.getPhotoUrl());
        response.setBio(artist.getBio());
        response.setFollowers(followers);
        response.setFollowed(followed);
        return response;
    }
}
