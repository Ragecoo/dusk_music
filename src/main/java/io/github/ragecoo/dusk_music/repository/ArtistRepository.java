package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist,Long> {

    Page<Artist> findByArtistNameContainingIgnoreCase(String artistName, Pageable pageable);
}
