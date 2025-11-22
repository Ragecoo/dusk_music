package io.github.ragecoo.dusk_music.dto.albumdto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumCreateRequest {
    @NotNull @Size(min = 1, max = 50)
    private String title;

    private String photoUrl;

    @NotNull
    private LocalDateTime releaseDate;

    @NotNull
    private Long artistId;


}