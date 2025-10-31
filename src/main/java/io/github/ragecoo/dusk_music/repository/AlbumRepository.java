package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Page<Album> findByArtistId(Long artistId, Pageable p);

    Page<Album> findAllByOrderByReleaseDateDesc(Pageable p);


}
