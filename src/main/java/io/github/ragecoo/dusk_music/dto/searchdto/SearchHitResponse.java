package io.github.ragecoo.dusk_music.dto.searchdto;

import lombok.*;


public record SearchHitResponse(

        String type,
        Long id,
        String name,
        String subtitle,
        String coverUrl
) {

}
