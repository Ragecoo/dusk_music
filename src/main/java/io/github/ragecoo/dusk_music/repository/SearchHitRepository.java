package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHitRepository extends JpaRepository<Track, Long> {

    @Query(value = """
        SELECT * FROM (
          SELECT 'track' AS type,
                 t.id     AS id,
                 t.title  AS name,
                 COALESCE(a.artist_name, '') || COALESCE(' · ' || al.title, '') AS subtitle,
                 t.cover_url AS coverUrl
          FROM tracks t
          LEFT JOIN artists a ON t.artist_id = a.id
          LEFT JOIN albums  al ON t.album_id  = al.id
          WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :target, '%'))

          UNION ALL

          SELECT 'artist' AS type,
                 ar.id     AS id,
                 ar.artist_name AS name,
                 NULL AS subtitle,
                 ar.photo_url AS coverUrl
          FROM artists ar
          WHERE LOWER(ar.artist_name) LIKE LOWER(CONCAT('%', :target, '%'))

          UNION ALL

          SELECT 'album' AS type,
                 al.id     AS id,
                 al.title  AS name,
                 ar.artist_name AS subtitle,
                 NULL AS coverUrl
          FROM albums al
          LEFT JOIN artists ar ON al.artist_id = ar.id
          WHERE LOWER(al.title) LIKE LOWER(CONCAT('%', :target, '%'))
        ) s
        ORDER BY s.name ASC
        """,
            countQuery = """
        SELECT (
          SELECT COUNT(*) FROM tracks t
          WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :target, '%'))
        ) + (
          SELECT COUNT(*) FROM artists ar
          WHERE LOWER(ar.artist_name) LIKE LOWER(CONCAT('%', :target, '%'))
        ) + (
          SELECT COUNT(*) FROM albums al
          WHERE LOWER(al.title) LIKE LOWER(CONCAT('%', :target, '%'))
        )
        """,
            nativeQuery = true)
    Page<SearchHit> searchAll(@Param("q") String target, Pageable pageable);
}
