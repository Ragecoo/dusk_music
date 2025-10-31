package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.UserArtistsFavorites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserArtistsFavoritesRepository extends JpaRepository<UserArtistsFavorites,Long> {

    boolean existsByUserIdAndArtistId(Long userId, Long artistId);

    Optional<UserArtistsFavorites> findByUserIdAndArtistId(Long userId, Long artistId);

    Page<UserArtistsFavorites> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Long countByArtistId(Long artistId);

    void deleteAllByUserId(Long userId);
}
