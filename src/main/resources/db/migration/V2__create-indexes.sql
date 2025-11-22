CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_albums_artist_id ON albums(artist_id);


CREATE INDEX IF NOT EXISTS idx_tracks_artist_id ON tracks(artist_id);
CREATE INDEX IF NOT EXISTS idx_tracks_album_id  ON tracks(album_id);

CREATE INDEX IF NOT EXISTS idx_playlists_owner_id ON playlists(user_id);


CREATE INDEX IF NOT EXISTS idx_tracks_play_count_desc ON tracks(play_count DESC);

CREATE INDEX IF NOT EXISTS idx_tracks_release_date_desc ON tracks(release_date DESC);
CREATE INDEX IF NOT EXISTS idx_albums_release_date_desc ON albums(release_date DESC);


CREATE INDEX IF NOT EXISTS idx_tracks_title_trgm
    ON tracks USING GIN (LOWER(title) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_artists_artist_name_trgm
    ON artists USING GIN (LOWER(artist_name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_albums_title_trgm
    ON albums USING GIN (LOWER(title) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_refresh_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_tokens(token);

CREATE INDEX IF NOT EXISTS idx_uaf_user ON user_artist_favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_uaf_artist ON user_artist_favorites(artist_id);

