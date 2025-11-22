package io.github.ragecoo.dusk_music.repository;

import io.github.ragecoo.dusk_music.model.PlayListTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListTrackRepository extends JpaRepository<PlayListTrack, Long> {

    boolean existsByPlaylistIdAndTrackId(Long playlistId, Long trackId);

    void deleteByPlaylistIdAndTrackId(Long playlistId, Long trackId);

    List<PlayListTrack> findByPlaylistIdOrderByPositionAsc(Long playlistId);

}
