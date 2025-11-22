package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.searchdto.SearchHitResponse;
import io.github.ragecoo.dusk_music.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "Контроллер для поиска треков, артистов и альбомов")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Универсальный поиск по трекам, артистам и альбомам")
    public ResponseEntity<Page<SearchHitResponse>> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SearchHitResponse> results = searchService.searchAll(query, pageable);
        return ResponseEntity.ok(results);
    }
}

