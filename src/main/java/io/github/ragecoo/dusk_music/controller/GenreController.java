package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.genredto.GenreCreateRequest;
import io.github.ragecoo.dusk_music.dto.genredto.GenreResponse;
import io.github.ragecoo.dusk_music.dto.genredto.GenreUpdateRequest;
import io.github.ragecoo.dusk_music.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
@Tag(name = "Genres", description = "Контроллер для работы с жанрами")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @Operation(summary = "Создать новый жанр")
    public ResponseEntity<GenreResponse> create(@Valid @RequestBody GenreCreateRequest request) {
        GenreResponse response = genreService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех жанров")
    public ResponseEntity<Page<GenreResponse>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<GenreResponse> genres = genreService.listAll(pageable);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить жанр по ID")
    public ResponseEntity<GenreResponse> get(@PathVariable Long id) {
        GenreResponse response = genreService.get(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить жанр")
    public ResponseEntity<GenreResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GenreUpdateRequest request) {
        GenreResponse response = genreService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить жанр")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

