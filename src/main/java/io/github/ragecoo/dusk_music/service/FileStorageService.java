package io.github.ragecoo.dusk_music.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    
    /**
     * Сохраняет аудиофайл
     * @param file файл для сохранения
     * @return относительный путь к сохраненному файлу
     * @throws IOException если не удалось сохранить файл
     */
    String saveAudioFile(MultipartFile file) throws IOException;
    
    /**
     * Сохраняет обложку трека
     * @param file файл обложки для сохранения
     * @return относительный путь к сохраненной обложке
     * @throws IOException если не удалось сохранить файл
     */
    String saveCoverFile(MultipartFile file) throws IOException;
    
    /**
     * Загружает файл как Resource для отдачи клиенту
     * @param fileName имя файла или путь
     * @return Resource с файлом
     * @throws IOException если файл не найден
     */
    Resource loadFileAsResource(String fileName) throws IOException;
    
    /**
     * Удаляет файл
     * @param fileName имя файла или путь
     * @throws IOException если не удалось удалить файл
     */
    void deleteFile(String fileName) throws IOException;
    
    /**
     * Проверяет существование файла
     * @param fileName имя файла или путь
     * @return true если файл существует
     */
    boolean fileExists(String fileName);
    
    /**
     * Получает путь к директории для аудиофайлов
     * @return Path к директории
     */
    Path getAudioDirectory();
    
    /**
     * Получает путь к директории для обложек
     * @return Path к директории
     */
    Path getCoverDirectory();
}

