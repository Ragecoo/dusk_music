# Новая упрощенная система проигрывания треков

## Что изменилось

### 1. Упрощенная архитектура
- ✅ Файлы доступны через простые HTTP URL без авторизации
- ✅ Убрана сложная логика с blob URL
- ✅ Прямое использование audio element с src
- ✅ Добавлен статический ресурс handler

### 2. Новые endpoints

#### Доступ к файлам:
- `http://localhost:8080/api/v1/files/audio/{filename}` - аудио файлы
- `http://localhost:8080/api/v1/files/covers/{filename}` - обложки
- `http://localhost:8080/files/{path}` - альтернативный путь (статический)

#### Безопасность:
- Файлы доступны без авторизации (для упрощения)
- Можно добавить проверку позже при необходимости

### 3. Формат audioUrl в БД

В базе данных `audio_url` должен храниться в формате:
- `audio/filename.mp3` - для аудио
- `covers/filename.jpg` - для обложек

Пример:
```sql
audio_url = 'audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3'
```

### 4. Frontend изменения

- Убрана функция `loadAudioWithAuth` (больше не нужна)
- Используется прямой URL: `audioPlayer.src = audioUrl`
- Упрощена логика формирования URL

## Как это работает

1. **Загрузка трека:**
   - Frontend получает трек из API: `/api/v1/tracks/{id}`
   - В ответе приходит `audioUrl: "audio/filename.mp3"`
   - Frontend формирует полный URL: `http://localhost:8080/api/v1/files/audio/filename.mp3`
   - Устанавливает в audio element: `audioPlayer.src = fullUrl`

2. **Сервер:**
   - FileController обрабатывает запрос `/api/v1/files/audio/{filename}`
   - FileStorageService загружает файл из `uploads/audio/{filename}`
   - Возвращает файл с правильными заголовками

3. **Docker:**
   - Локальная директория `./uploads` монтируется в `/opt/app/uploads`
   - Файлы доступны сразу без копирования

## Проверка работы

### 1. Убедитесь, что файлы есть:
```bash
ls -la uploads/audio/
```

### 2. Проверьте URL в БД:
```sql
SELECT id, title, audio_url FROM tracks LIMIT 1;
```
Должно быть: `audio/filename.mp3`

### 3. Проверьте доступность файла:
```bash
curl http://localhost:8080/api/v1/files/audio/3fbdf81c-0267-443e-bae1-6eb04b386ed1.mp3
```

### 4. Проверьте в браузере:
Откройте консоль (F12) и попробуйте воспроизвести трек. Должны увидеть:
- Успешную загрузку трека
- Прямой URL в audio element
- Воспроизведение без ошибок

## Миграция существующих данных

Если у вас уже есть треки в БД с неправильным форматом `audio_url`:

```sql
-- Проверьте текущий формат
SELECT id, audio_url FROM tracks;

-- Если нужно обновить (пример)
UPDATE tracks 
SET audio_url = 'audio/' || SUBSTRING(audio_url FROM POSITION('/' IN audio_url) + 1)
WHERE audio_url NOT LIKE 'audio/%' AND audio_url LIKE '%/%';
```

## Устранение проблем

### Файл не найден (404)
1. Проверьте, что файл существует: `ls uploads/audio/{filename}`
2. Проверьте формат в БД: `SELECT audio_url FROM tracks WHERE id = ?`
3. Проверьте логи: `docker-compose logs app | grep "File"`

### CORS ошибки
- Убедитесь, что в SpringSecurityConfiguration разрешен доступ к `/api/v1/files/**`
- Проверьте CORS настройки в браузере

### Файлы не видны в Docker
- Проверьте монтирование: `docker-compose exec app ls -la /opt/app/uploads/audio/`
- Убедитесь, что локальная директория `uploads/` существует

## Преимущества новой системы

1. **Простота** - меньше кода, проще понять
2. **Надежность** - меньше точек отказа
3. **Производительность** - прямой доступ к файлам
4. **Отладка** - проще найти проблемы
5. **Масштабируемость** - легко добавить кэширование/CDN

