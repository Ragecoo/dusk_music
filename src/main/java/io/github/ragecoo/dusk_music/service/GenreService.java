package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.genredto.GenreCreateRequest;
import io.github.ragecoo.dusk_music.dto.genredto.GenreResponse;
import io.github.ragecoo.dusk_music.dto.genredto.GenreUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {

    GenreResponse create(GenreCreateRequest request);

    GenreResponse get(Long genreId);

    GenreResponse update(Long genreId, GenreUpdateRequest request);

    void delete(Long genreId);

    Page<GenreResponse> listAll(Pageable pageable);
}

