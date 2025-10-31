package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistCreateRequest;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.model.Artist;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public ArtistRef toRef(Artist artist){
        return new ArtistRef(artist.getId(), artist.getArtistName(), artist.getPhotoUrl());
    }

    public Artist toEntity(ArtistCreateRequest request){

        Artist artist= new Artist();
        artist.setArtistName(request.getName());
        artist.setPhotoUrl(request.getPhotoUrl());
        artist.setBio(request.getBio());

        return artist;
    }




}
