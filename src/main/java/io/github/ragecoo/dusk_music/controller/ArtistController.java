package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.artistdto.ArtistCreateRequest;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistResponse;
import io.github.ragecoo.dusk_music.dto.artistdto.ArtistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.trackdto.TrackShortResponse;
import io.github.ragecoo.dusk_music.dto.userdto.AuthUser;
import io.github.ragecoo.dusk_music.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@Tag(name = "Artists", description = "Контроллер для работы с артистами")
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    @Operation(summary = "Создать нового артиста")
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistCreateRequest request) {
        ArtistResponse response = artistService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех артистов")
    public ResponseEntity<Page<ArtistResponse>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ArtistResponse> artists = artistService.listAll(pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск артистов по имени")
    public ResponseEntity<Page<ArtistResponse>> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ArtistResponse> artists = artistService.search(query, pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить артиста по ID")
    public ResponseEntity<ArtistResponse> get(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        ArtistResponse response = artistService.get(currentUserId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить артиста")
    public ResponseEntity<ArtistResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ArtistUpdateRequest request) {
        ArtistResponse response = artistService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить артиста")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tracks")
    @Operation(summary = "Получить треки артиста")
    public ResponseEntity<Page<TrackShortResponse>> listTracks(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrackShortResponse> tracks = artistService.listTracks(id, pageable);
        return ResponseEntity.ok(tracks);
    }

    @PostMapping("/{id}/follow")
    @Operation(summary = "Подписаться на артиста")
    public ResponseEntity<Void> follow(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        artistService.follow(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    @Operation(summary = "Отписаться от артиста")
    public ResponseEntity<Void> unfollow(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        artistService.unfollow(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-favorites")
    @Operation(summary = "Получить список избранных артистов текущего пользователя")
    public ResponseEntity<Page<ArtistResponse>> myFavorites(
            @PageableDefault(size = 20) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<ArtistResponse> favorites = artistService.myFavorites(userId, pageable);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Получает ID текущего аутентифицированного пользователя из SecurityContext
     * @return ID пользователя
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser authUser) {
            return authUser.id();
        }
        return null;
    }
}

