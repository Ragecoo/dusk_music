package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    Page<Track> findByArtistId(Long artistId, Pageable p);

    Page<Track> findByAlbumId(Long albumId, Pageable p);

    Page<Track> findByGenreId(Long genreId, Pageable p);

    Page<Track> findAllByOrderByPlayCountDesc(Pageable p);

    Page<Track> findAllByOrderByReleaseDateDesc(Pageable p);

    Page<Track> findByTitleContainingIgnoreCase(String title, Pageable p);
}
