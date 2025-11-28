# Формат данных в базе данных

## Ответы на ваши вопросы

### 1. Нужно ли менять миграции?

**НЕТ, менять миграции НЕ нужно!**

Структура таблицы `tracks` не изменилась:
- `audio_url` - это просто `varchar` (строка)
- `cover_url` - это просто `varchar` (строка)

**НО!** Если у вас уже есть данные в БД, и они в неправильном формате, создана миграция `V3__normalize-file-paths.sql`, которая автоматически обновит существующие записи к правильному формату.

### 2. Как должны выглядеть данные в БД?

## Формат `audio_url` и `cover_url`

### ✅ ПРАВИЛЬНЫЙ формат:

```sql
audio_url = 'audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3'
cover_url = 'covers/cover-image.jpg'
```

**Правила:**
- Начинается с `audio/` для аудио файлов
- Начинается с `covers/` для обложек
- После префикса идет имя файла (например, UUID + расширение)
- БЕЗ начального слэша `/`
- БЕЗ полного URL `http://...`
- БЕЗ префиксов `/api/v1/files/` или `/files/`

### ❌ НЕПРАВИЛЬНЫЕ форматы:

```sql
-- НЕПРАВИЛЬНО - полный URL
audio_url = 'http://localhost:8080/api/v1/files/audio/file.mp3'

-- НЕПРАВИЛЬНО - с начальным слэшем
audio_url = '/audio/file.mp3'

-- НЕПРАВИЛЬНО - с префиксом API
audio_url = '/api/v1/files/audio/file.mp3'

-- НЕПРАВИЛЬНО - только имя файла без префикса
audio_url = 'file.mp3'
```

## Примеры правильных данных

### Пример 1: Трек с аудио и обложкой

```sql
INSERT INTO tracks (title, artist_id, album_id, duration, audio_url, cover_url, release_date)
VALUES (
    'NumbCorrect',
    1,
    1,
    186,
    'audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3',  -- ✅ Правильно
    'covers/cover-123.jpg',                             -- ✅ Правильно
    '2003-03-25 00:00:00'
);
```

### Пример 2: Проверка текущих данных

```sql
-- Посмотреть текущий формат
SELECT id, title, audio_url, cover_url FROM tracks;

-- Должно быть:
-- id | title        | audio_url                                    | cover_url
-- 1  | NumbCorrect  | audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3 | covers/cover.jpg
```

## Как проверить и исправить данные

### Шаг 1: Проверьте текущий формат

```sql
SELECT 
    id,
    title,
    audio_url,
    CASE 
        WHEN audio_url LIKE 'audio/%' THEN '✅ Правильно'
        ELSE '❌ Нужно исправить'
    END as audio_status,
    cover_url,
    CASE 
        WHEN cover_url LIKE 'covers/%' THEN '✅ Правильно'
        ELSE '❌ Нужно исправить'
    END as cover_status
FROM tracks;
```

### Шаг 2: Если есть неправильные данные

Миграция `V3__normalize-file-paths.sql` автоматически исправит их при следующем запуске приложения.

Или исправьте вручную:

```sql
-- Пример: если audio_url = '/api/v1/files/audio/file.mp3'
UPDATE tracks 
SET audio_url = 'audio/file.mp3'
WHERE audio_url LIKE '/api/v1/files/audio/%';

-- Пример: если audio_url = 'file.mp3' (без префикса)
UPDATE tracks 
SET audio_url = 'audio/' || audio_url
WHERE audio_url NOT LIKE 'audio/%' AND audio_url NOT LIKE 'covers/%';
```

### Шаг 3: Проверьте после исправления

```sql
-- Все должно быть в правильном формате
SELECT id, title, audio_url, cover_url 
FROM tracks 
WHERE audio_url NOT LIKE 'audio/%' OR cover_url NOT LIKE 'covers/%';
-- Этот запрос должен вернуть 0 строк
```

## Как данные используются в приложении

1. **В БД хранится:** `audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3`

2. **Frontend получает из API:**
   ```json
   {
     "id": 1,
     "title": "NumbCorrect",
     "audioUrl": "audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3"
   }
   ```

3. **Frontend формирует полный URL:**
   ```javascript
   audioUrl = `http://localhost:8080/api/v1/files/${track.audioUrl}`
   // Результат: http://localhost:8080/api/v1/files/audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3
   ```

4. **Сервер обрабатывает запрос:**
   - Получает: `/api/v1/files/audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3`
   - Извлекает: `audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3`
   - Загружает файл из: `uploads/audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3`

## Резюме

- ✅ **Миграции менять НЕ нужно** - структура таблицы не изменилась
- ✅ **Формат в БД:** `audio/filename.mp3` и `covers/filename.jpg`
- ✅ **Миграция V3** автоматически исправит существующие данные
- ✅ **Новые данные** должны сохраняться в правильном формате

Если у вас есть вопросы или проблемы - проверьте логи приложения и формат данных в БД!

