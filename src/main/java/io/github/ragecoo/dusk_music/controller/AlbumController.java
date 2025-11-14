package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.albumdto.AlbumCreateRequest;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumResponse;
import io.github.ragecoo.dusk_music.dto.albumdto.AlbumUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.service.AlbumService;
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
@RequestMapping("/api/v1/albums")
@Tag(name = "Albums", description = "Контроллер для работы с альбомами")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @Operation(summary = "Создать новый альбом")
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumCreateRequest request) {
        AlbumResponse response = albumService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех альбомов")
    public ResponseEntity<Page<AlbumResponse>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AlbumResponse> albums = albumService.listAll(pageable);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить альбом по ID")
    public ResponseEntity<AlbumResponse> get(@PathVariable Long id) {
        AlbumResponse response = albumService.get(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить альбом")
    public ResponseEntity<AlbumResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AlbumUpdateRequest request) {
        AlbumResponse response = albumService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить альбом")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Получить альбомы артиста")
    public ResponseEntity<Page<AlbumResponse>> listByArtist(
            @PathVariable Long artistId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AlbumResponse> albums = albumService.listByArtist(artistId, pageable);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}/tracks")
    @Operation(summary = "Получить треки альбома")
    public ResponseEntity<Page<TrackShortResponse>> listTracks(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackShortResponse> tracks = albumService.listTracks(id, pageable);
        return ResponseEntity.ok(tracks);
    }
}

