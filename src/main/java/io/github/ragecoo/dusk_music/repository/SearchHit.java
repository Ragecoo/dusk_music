package io.github.ragecoo.dusk_music.repository;

public interface SearchHit {

    String getType();
    Long   getId();
    String getName();
    String getSubtitle();
    String getCoverUrl();
}
