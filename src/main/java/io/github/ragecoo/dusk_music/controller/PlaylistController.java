package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.dto.playlistdto.AddTrackToPlaylistRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistCreateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistResponse;
import io.github.ragecoo.dusk_music.dto.playlistdto.PlaylistUpdateRequest;
import io.github.ragecoo.dusk_music.dto.playlistdto.RemoveTrackFromPlaylistRequest;
import io.github.ragecoo.dusk_music.dto.userdto.AuthUser;
import io.github.ragecoo.dusk_music.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlists")
@Tag(name = "Playlists", description = "Контроллер для работы с плейлистами")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    @Operation(summary = "Создать новый плейлист")
    public ResponseEntity<PlaylistResponse> create(@Valid @RequestBody PlaylistCreateRequest request) {
        Long userId = getCurrentUserId();
        PlaylistResponse response = playlistService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить плейлист по ID")
    public ResponseEntity<PlaylistResponse> get(@PathVariable Long id) {
        PlaylistResponse response = playlistService.get(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить плейлист")
    public ResponseEntity<PlaylistResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PlaylistUpdateRequest request) {
        Long userId = getCurrentUserId();
        PlaylistResponse response = playlistService.update(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить плейлист")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        playlistService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-playlists")
    @Operation(summary = "Получить список плейлистов текущего пользователя")
    public ResponseEntity<List<PlaylistResponse>> myPlaylists() {
        Long userId = getCurrentUserId();
        List<PlaylistResponse> playlists = playlistService.listByUser(userId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/my-favorite")
    @Operation(summary = "Получить избранный плейлист текущего пользователя")
    public ResponseEntity<PlaylistResponse> myFavorite() {
        Long userId = getCurrentUserId();
        PlaylistResponse response = playlistService.getFavorite(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tracks")
    @Operation(summary = "Добавить трек в плейлист")
    public ResponseEntity<Void> addTrack(@Valid @RequestBody AddTrackToPlaylistRequest request) {
        Long userId = getCurrentUserId();
        playlistService.addTrack(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tracks")
    @Operation(summary = "Удалить трек из плейлиста")
    public ResponseEntity<Void> removeTrack(@Valid @RequestBody RemoveTrackFromPlaylistRequest request) {
        Long userId = getCurrentUserId();
        playlistService.removeTrack(userId, request);
        return ResponseEntity.ok().build();
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

