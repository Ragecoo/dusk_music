-- Миграция для нормализации путей к файлам
-- Обновляет audio_url и cover_url к правильному формату: "audio/filename.mp3" или "covers/filename.jpg"

-- Обновляем audio_url: убираем префиксы и добавляем "audio/" если нужно
UPDATE tracks
SET audio_url = CASE
    -- Если уже в правильном формате "audio/..." - оставляем как есть
    WHEN audio_url LIKE 'audio/%' THEN audio_url
    -- Если начинается с "/api/v1/files/audio/" - убираем префикс до "audio/"
    WHEN audio_url LIKE '/api/v1/files/audio/%' THEN 'audio/' || SUBSTRING(audio_url FROM LENGTH('/api/v1/files/audio/') + 1)
    -- Если начинается с "/files/audio/" - убираем префикс до "audio/"
    WHEN audio_url LIKE '/files/audio/%' THEN 'audio/' || SUBSTRING(audio_url FROM LENGTH('/files/audio/') + 1)
    -- Если начинается с "/uploads/audio/" - убираем префикс до "audio/"
    WHEN audio_url LIKE '/uploads/audio/%' THEN 'audio/' || SUBSTRING(audio_url FROM LENGTH('/uploads/audio/') + 1)
    -- Если начинается с "/audio/" - убираем первый слэш
    WHEN audio_url LIKE '/audio/%' THEN SUBSTRING(audio_url FROM 2)
    -- Если это просто имя файла без слэшей - добавляем "audio/"
    WHEN audio_url NOT LIKE '%/%' THEN 'audio/' || audio_url
    -- Если это полный URL или путь с слэшами - извлекаем имя файла после последнего слэша
    ELSE 'audio/' || SPLIT_PART(audio_url, '/', -1)
END
WHERE audio_url IS NOT NULL AND audio_url NOT LIKE 'audio/%';

-- Обновляем cover_url аналогично
UPDATE tracks
SET cover_url = CASE
    -- Если уже в правильном формате "covers/..." - оставляем как есть
    WHEN cover_url LIKE 'covers/%' THEN cover_url
    -- Если начинается с "/api/v1/files/covers/" - убираем префикс до "covers/"
    WHEN cover_url LIKE '/api/v1/files/covers/%' THEN 'covers/' || SUBSTRING(cover_url FROM LENGTH('/api/v1/files/covers/') + 1)
    -- Если начинается с "/files/covers/" - убираем префикс до "covers/"
    WHEN cover_url LIKE '/files/covers/%' THEN 'covers/' || SUBSTRING(cover_url FROM LENGTH('/files/covers/') + 1)
    -- Если начинается с "/uploads/covers/" - убираем префикс до "covers/"
    WHEN cover_url LIKE '/uploads/covers/%' THEN 'covers/' || SUBSTRING(cover_url FROM LENGTH('/uploads/covers/') + 1)
    -- Если начинается с "/covers/" - убираем первый слэш
    WHEN cover_url LIKE '/covers/%' THEN SUBSTRING(cover_url FROM 2)
    -- Если это просто имя файла без слэшей - добавляем "covers/"
    WHEN cover_url NOT LIKE '%/%' THEN 'covers/' || cover_url
    -- Если это полный URL или путь с слэшами - извлекаем имя файла после последнего слэша
    ELSE 'covers/' || SPLIT_PART(cover_url, '/', -1)
END
WHERE cover_url IS NOT NULL AND cover_url NOT LIKE 'covers/%';

