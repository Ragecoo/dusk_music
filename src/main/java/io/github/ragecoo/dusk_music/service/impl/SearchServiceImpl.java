package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.searchdto.SearchHitResponse;
import io.github.ragecoo.dusk_music.repository.SearchHit;
import io.github.ragecoo.dusk_music.repository.SearchHitRepository;
import io.github.ragecoo.dusk_music.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchHitRepository searchHitRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SearchHitResponse> searchAll(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            // Возвращаем пустую страницу, если запрос пустой
            return Page.empty(pageable);
        }

        String searchQuery = query.trim();
        Page<SearchHit> searchHits = searchHitRepository.searchAll(searchQuery, pageable);

        return searchHits.map(this::toResponse);
    }

    /**
     * Преобразует SearchHit в SearchHitResponse
     */
    private SearchHitResponse toResponse(SearchHit searchHit) {
        return new SearchHitResponse(
                searchHit.getType(),
                searchHit.getId(),
                searchHit.getName(),
                searchHit.getSubtitle(),
                searchHit.getCoverUrl()
        );
    }
}

