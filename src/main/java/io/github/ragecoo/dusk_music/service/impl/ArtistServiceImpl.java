package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
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

    @Override
    @Transactional
    public ArtistResponse get(Long currentUserId, Long artistId) {
        Artist a = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));
        long followers = favoritesRepository.countByArtistId(artistId);
        boolean followed = currentUserId != null && favoritesRepository.existsByUserIdAndArtistId(currentUserId, artistId);

        ArtistResponse dto = new ArtistResponse();
        dto.setId(a.getId());
        dto.setName(a.getArtistName());
        dto.setPhotoUrl(a.getPhotoUrl());
        dto.setBio(a.getBio());
        dto.setFollowers(followers);
        dto.setFollowed(followed);
        return dto;
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


        return page.map(f-> {
            ArtistResponse response= new ArtistResponse();
            response.setId(f.getId());
            response.setName(f.getArtist().getArtistName());
            response.setPhotoUrl(f.getArtist().getPhotoUrl());
            response.setFollowers(favoritesRepository.countByArtistId(f.getArtist().getId()));
            response.setFollowed(true);
            return response;

        });

    }

    @Override
    @Transactional
    public long followersCount(Long artistId) {
        return favoritesRepository.countByArtistId(artistId);
    }
}
