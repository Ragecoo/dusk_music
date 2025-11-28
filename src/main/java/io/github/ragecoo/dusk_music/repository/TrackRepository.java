package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findByArtistId(Long artistId, Pageable p);

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findByAlbumId(Long albumId, Pageable p);

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findByGenreId(Long genreId, Pageable p);

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findAllByOrderByPlayCountDesc(Pageable p);

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findAllByOrderByReleaseDateDesc(Pageable p);

    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findByTitleContainingIgnoreCase(String title, Pageable p);

    @Override
    @EntityGraph(attributePaths = {"album", "artist", "genre"})
    Page<Track> findAll(Pageable pageable);
}
