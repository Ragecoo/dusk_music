package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Long> {

    Optional<Genre> findByTitleIgnoreCase(String title);
}
