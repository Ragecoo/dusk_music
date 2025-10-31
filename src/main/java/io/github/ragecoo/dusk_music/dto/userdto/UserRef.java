package io.github.ragecoo.dusk_music.dto.userdto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Мини-ссылка на пользователя (для вложенных DTO)")

public record UserRef (
     Long id,
     String username,
     String avatarUrl
){
}
