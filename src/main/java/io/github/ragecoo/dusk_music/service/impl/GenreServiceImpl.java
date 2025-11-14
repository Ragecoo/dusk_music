package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.dto.genredto.GenreCreateRequest;
import io.github.ragecoo.dusk_music.dto.genredto.GenreResponse;
import io.github.ragecoo.dusk_music.dto.genredto.GenreUpdateRequest;
import io.github.ragecoo.dusk_music.exceptions.NotFoundException;
import io.github.ragecoo.dusk_music.exceptions.TakenException;
import io.github.ragecoo.dusk_music.mapper.GenreMapper;
import io.github.ragecoo.dusk_music.model.Genre;
import io.github.ragecoo.dusk_music.repository.GenreRepository;
import io.github.ragecoo.dusk_music.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    @Transactional
    public GenreResponse create(GenreCreateRequest request) {
        // Проверка на уникальность названия жанра
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            if (genreRepository.findByTitleIgnoreCase(request.getTitle().trim()).isPresent()) {
                throw new TakenException("Genre with this title already exists");
            }
        }

        Genre genre = genreMapper.toEntity(request);
        if (genre.getTitle() != null) {
            genre.setTitle(genre.getTitle().trim());
        }
        genre = genreRepository.save(genre);

        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse get(Long genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found"));

        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public GenreResponse update(Long genreId, GenreUpdateRequest request) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new NotFoundException("Genre not found"));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            String newTitle = request.getTitle().trim();
            // Проверка на уникальность названия (кроме текущего жанра)
            genreRepository.findByTitleIgnoreCase(newTitle)
                    .ifPresent(existingGenre -> {
                        if (!existingGenre.getId().equals(genreId)) {
                            throw new TakenException("Genre with this title already exists");
                        }
                    });
            genre.setTitle(newTitle);
        }

        genre = genreRepository.save(genre);
        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public void delete(Long genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw new NotFoundException("Genre not found");
        }
        genreRepository.deleteById(genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreResponse> listAll(Pageable pageable) {
        Page<Genre> genres = genreRepository.findAll(pageable);
        return genres.map(genreMapper::toResponse);
    }
}

