package io.github.ragecoo.dusk_music.mapper;

import io.github.ragecoo.dusk_music.dto.genredto.GenreCreateRequest;
import io.github.ragecoo.dusk_music.dto.genredto.GenreRef;
import io.github.ragecoo.dusk_music.dto.genredto.GenreResponse;
import io.github.ragecoo.dusk_music.model.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreRef toRef(Genre genre){
        return new GenreRef(genre.getId(), genre.getTitle());
    }

    public Genre toEntity(GenreCreateRequest request){
        Genre genre= new Genre();
        genre.setTitle(request.getTitle());

        return genre;
    }

    public GenreResponse toResponse(Genre genre){
        return new GenreResponse(genre.getId(), genre.getTitle());
    }
}
