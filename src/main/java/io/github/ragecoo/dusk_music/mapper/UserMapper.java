package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.userdto.UserRef;
import io.github.ragecoo.dusk_music.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserRef toRef(User user){
        return new UserRef(user.getId(), user.getUsername(), user.getAvatarUrl());
    }

}
