package io.github.ragecoo.dusk_music.service;

import io.github.ragecoo.dusk_music.dto.searchdto.SearchHitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    Page<SearchHitResponse> searchAll(String query, Pageable pageable);
}

