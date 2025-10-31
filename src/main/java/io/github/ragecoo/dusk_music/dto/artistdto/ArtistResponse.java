package io.github.ragecoo.dusk_music.dto.artistdto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistResponse{
    private Long id;
    private String name;
    private String photoUrl;
    private String bio;
    private Long followers;
    private boolean followed;


}
