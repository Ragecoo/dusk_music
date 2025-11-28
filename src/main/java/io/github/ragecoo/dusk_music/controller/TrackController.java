package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.trackdto.TrackCreateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackResponse;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackUpdateRequest;
import io.github.ragecoo.dusk_music.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tracks")
@Tag(name = "Tracks", description = "Контроллер для работы с треками")
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    @Operation(summary = "Создать новый трек")
    public ResponseEntity<TrackResponse> create(@Valid @RequestBody TrackCreateRequest request) {
        TrackResponse response = trackService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех треков")
    public ResponseEntity<Page<TrackResponse>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listAll(pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/popular")
    @Operation(summary = "Получить популярные треки")
    public ResponseEntity<Page<TrackResponse>> listPopular(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listPopular(pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/recent")
    @Operation(summary = "Получить недавние треки")
    public ResponseEntity<Page<TrackResponse>> listRecent(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listRecent(pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск треков по названию")
    public ResponseEntity<Page<TrackResponse>> search(
            @RequestParam String title,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.searchByTitle(title, pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Получить треки артиста")
    public ResponseEntity<Page<TrackResponse>> listByArtist(
            @PathVariable Long artistId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listByArtist(artistId, pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/album/{albumId}")
    @Operation(summary = "Получить треки альбома")
    public ResponseEntity<Page<TrackResponse>> listByAlbum(
            @PathVariable Long albumId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listByAlbum(albumId, pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/genre/{genreId}")
    @Operation(summary = "Получить треки жанра")
    public ResponseEntity<Page<TrackResponse>> listByGenre(
            @PathVariable Long genreId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackResponse> tracks = trackService.listByGenre(genreId, pageable);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить трек по ID")
    public ResponseEntity<TrackResponse> get(@PathVariable Long id) {
        log.info("=== Get track request ===");
        log.info("Track ID: {}", id);
        try {
            TrackResponse response = trackService.get(id);
            log.info("Track found: id={}, title={}, audioUrl={}", 
                    response.id(), response.title(), response.audioUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting track with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить трек")
    public ResponseEntity<TrackResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TrackUpdateRequest request) {
        TrackResponse response = trackService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить трек")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trackService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/play")
    @Operation(summary = "Увеличить счетчик прослушиваний трека")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable Long id) {
        trackService.incrementPlayCount(id);
        return ResponseEntity.ok().build();
    }
}

