package io.github.ragecoo.dusk_music.dto.playlistdto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistCreateRequest {

    @NotNull
    @Size(max = 50)
    private String name;

    private Long userId;






}
