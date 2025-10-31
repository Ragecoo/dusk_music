package io.github.ragecoo.dusk_music.dto.playlistdto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveTrackFromPlaylistRequest {
    @NotNull
    Long playlistId;
    @NotNull
    Long trackId;
}


