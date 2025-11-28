# Руководство по миграции системы хранения файлов

## Проблема
Файлы (аудио и обложки) хранились внутри Docker контейнера и терялись при перезапуске.

## Решение
Добавлен персистентный Docker volume для хранения файлов.

## Шаги миграции

### 1. Остановите текущие контейнеры
```bash
docker-compose down
```

### 2. Пересоберите образ приложения
```bash
docker-compose build app
```

### 3. Запустите контейнеры с новым volume
```bash
docker-compose up -d
```

### 4. Проверьте, что volume создан
```bash
docker volume ls | grep app_uploads
```

### 5. Проверьте логи приложения
```bash
docker-compose logs app | grep "Audio files will be stored"
```
Должно показать: `Audio files will be stored in: /opt/app/uploads/audio`

### 6. Если у вас были файлы в старой системе
Если у вас были загруженные файлы, которые нужно восстановить:

#### Вариант A: Копирование из старого контейнера (если он еще работает)
```bash
# Найдите ID старого контейнера
docker ps -a | grep dusk

# Скопируйте файлы из старого контейнера
docker cp <old-container-id>:/opt/app/uploads ./backup_uploads

# Скопируйте в новый volume
docker run --rm -v app_uploads:/uploads -v $(pwd)/backup_uploads:/backup alpine sh -c "cp -r /backup/* /uploads/"
```

#### Вариант B: Загрузка файлов заново
Если файлы потеряны, их нужно загрузить заново через API.

### 7. Проверка работы
1. Загрузите тестовый трек через API
2. Проверьте, что файл сохранился:
   ```bash
   docker run --rm -v app_uploads:/uploads alpine ls -la /uploads/audio/
   ```
3. Попробуйте воспроизвести трек в веб-интерфейсе

## Структура volume

```
app_uploads/
├── audio/          # Аудиофайлы (MP3, WAV и т.д.)
└── covers/         # Обложки (JPG, PNG и т.д.)
```

## Резервное копирование

### Создание бэкапа
```bash
docker run --rm -v app_uploads:/uploads -v $(pwd):/backup alpine tar czf /backup/uploads_backup_$(date +%Y%m%d).tar.gz -C /uploads .
```

### Восстановление из бэкапа
```bash
docker run --rm -v app_uploads:/uploads -v $(pwd):/backup alpine sh -c "cd /uploads && tar xzf /backup/uploads_backup_YYYYMMDD.tar.gz"
```

## Удаление volume (ОСТОРОЖНО!)
Если нужно полностью удалить все файлы:
```bash
docker-compose down -v
```
⚠️ Это удалит ВСЕ данные, включая базу данных!

## Альтернативные решения (на будущее)

Если в будущем понадобится более масштабируемое решение, можно рассмотреть:

1. **MinIO** - S3-совместимое хранилище
2. **AWS S3** - облачное хранилище
3. **NFS** - сетевая файловая система
4. **Ceph** - распределенное хранилище

Текущее решение с Docker volume подходит для:
- Разработки
- Небольших/средних проектов
- Одиночного сервера

