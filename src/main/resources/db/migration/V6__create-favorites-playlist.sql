-- Создание плейлиста "Favorites" для пользователя nikita123
-- Если плейлист уже существует, миграция не создаст дубликат

INSERT INTO playlists (user_id, name, is_favorite, created_at)
SELECT 
    u.id,
    'Favorites',
    true,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.username = 'nikita123'
  AND NOT EXISTS (
      SELECT 1 
      FROM playlists p 
      WHERE p.user_id = u.id 
        AND p.name = 'Favorites'
        AND p.is_favorite = true
  );

