package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumCreateRequest;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumResponse;
import io.github.ragecoo.dusk_music.model.Album;
import io.github.ragecoo.dusk_music.model.Artist;
import lombok.*;
import org.springframework.stereotype.Component;


@Component
public class AlbumMapper {

    public AlbumRef toRef(Album album){

        return new AlbumRef(album.getId(), album.getTitle(), album.getPhotoUrl());

    }

    public Album toEntity(AlbumCreateRequest request, Artist artist){
        Album album= new Album();
        album.setTitle(request.getTitle());
        album.setPhotoUrl(request.getPhotoUrl());
        album.setReleaseDate(request.getReleaseDate());
        album.setArtist(artist);
        return album;
    }

    public AlbumResponse toResponse(Album album){

        return new AlbumResponse(album.getId(), album.getTitle(),album.getPhotoUrl(), album.getReleaseDate(), album.getArtist().getId());

    }
}
