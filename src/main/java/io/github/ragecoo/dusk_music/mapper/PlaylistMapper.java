package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistCreateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistResponse;
import io.github.ragecoo.dusk_music.dto.userdto.UserRef;
import io.github.ragecoo.dusk_music.model.Playlist;
import io.github.ragecoo.dusk_music.model.User;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMapper {

    public Playlist toEntity(PlaylistCreateRequest request, User user){
        Playlist playlist= new Playlist();

        playlist.setName(request.getName());
        playlist.setUser(user);

        return playlist;
    }


}
