package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumRef;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.model.Album;
import io.github.ragecoo.dusk_music.model.Artist;
import io.github.ragecoo.dusk_music.model.Genre;
import io.github.ragecoo.dusk_music.model.Track;
import org.springframework.stereotype.Component;

@Component
public class TrackMapper {

    public Track toEntity(TrackCreateRequest request, Album album, Artist artist, Genre genre){
        Track track= new Track();

        track.setTitle(request.getTitle());
        track.setGenre(genre);
        track.setAlbum(album);
        track.setArtist(artist);
        track.setAudioUrl(request.getAudioUrl());
        track.setCoverUrl(request.getCoverUrl());
        track.setReleaseDate(request.getReleaseDate());

        return track;

    }

    public TrackResponse toResponse(Track track, ArtistRef artistRef, AlbumRef albumRef, GenreRef genreRef){
        return new TrackResponse(track.getId(),track.getTitle(),track.getDuration(),track.getAudioUrl()
        ,track.getCoverUrl(),track.getPlayCount(),track.getReleaseDate(), artistRef, albumRef, genreRef);
    }

}
