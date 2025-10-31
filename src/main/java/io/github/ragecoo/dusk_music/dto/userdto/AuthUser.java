package io.github.ragecoo.dusk_music.dto.userdto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AuthUser(Long id, String username,
                       Collection<? extends GrantedAuthority> authorities) {
}