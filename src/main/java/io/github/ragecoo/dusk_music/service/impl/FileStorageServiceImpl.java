package io.github.ragecoo.dusk_music.service.impl;

import io.github.ragecoo.dusk_music.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path audioStorageLocation;
    private final Path coverStorageLocation;

    public FileStorageServiceImpl(@Value("${file.storage.audio-dir:uploads/audio}") String audioDir,
                                  @Value("${file.storage.cover-dir:uploads/covers}") String coverDir) throws IOException {
        this.audioStorageLocation = Paths.get(audioDir).toAbsolutePath().normalize();
        this.coverStorageLocation = Paths.get(coverDir).toAbsolutePath().normalize();
        
        // Создаем директории, если они не существуют
        Files.createDirectories(this.audioStorageLocation);
        Files.createDirectories(this.coverStorageLocation);
        
        log.info("Audio files will be stored in: {}", this.audioStorageLocation);
        log.info("Cover images will be stored in: {}", this.coverStorageLocation);
    }

    @Override
    public String saveAudioFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty audio file");
        }
        
        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;
        
        Path targetLocation = this.audioStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        log.debug("Audio file saved: {}", targetLocation);
        
        // Возвращаем относительный путь для хранения в БД
        return "audio/" + fileName;
    }

    @Override
    public String saveCoverFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty cover file");
        }
        
        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;
        
        Path targetLocation = this.coverStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        log.debug("Cover file saved: {}", targetLocation);
        
        // Возвращаем относительный путь для хранения в БД
        return "covers/" + fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws IOException {
        try {
            log.info("Loading file resource: {}", fileName);
            Path filePath;
            
            // Определяем тип файла по префиксу пути
            if (fileName.startsWith("audio/")) {
                String actualFileName = fileName.substring("audio/".length());
                filePath = this.audioStorageLocation.resolve(actualFileName).normalize();
                log.info("Audio file path resolved to: {}", filePath);
                log.info("Audio storage location: {}", this.audioStorageLocation);
            } else if (fileName.startsWith("covers/")) {
                String actualFileName = fileName.substring("covers/".length());
                filePath = this.coverStorageLocation.resolve(actualFileName).normalize();
                log.info("Cover file path resolved to: {}", filePath);
            } else {
                // Пробуем найти в обеих директориях
                Path audioPath = this.audioStorageLocation.resolve(fileName).normalize();
                Path coverPath = this.coverStorageLocation.resolve(fileName).normalize();
                
                if (Files.exists(audioPath)) {
                    filePath = audioPath;
                    log.info("Found file in audio directory: {}", filePath);
                } else if (Files.exists(coverPath)) {
                    filePath = coverPath;
                    log.info("Found file in covers directory: {}", filePath);
                } else {
                    log.error("File not found in any directory. Tried: {} and {}", audioPath, coverPath);
                    throw new IOException("File not found: " + fileName);
                }
            }
            
            // Проверка на путь-траверсал (security)
            if (filePath.startsWith(this.audioStorageLocation) || filePath.startsWith(this.coverStorageLocation)) {
                log.info("File path security check passed: {}", filePath);
                log.info("File exists: {}", Files.exists(filePath));
                
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    log.info("File resource loaded successfully: {}", filePath);
                    return resource;
                } else {
                    log.error("File exists but not readable. Path: {}, Exists: {}, Readable: {}", 
                             filePath, resource.exists(), resource.isReadable());
                    throw new IOException("File not readable: " + fileName);
                }
            } else {
                log.error("Security check failed. File path: {}, Audio location: {}, Cover location: {}", 
                         filePath, this.audioStorageLocation, this.coverStorageLocation);
                throw new IOException("Invalid file path: " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("Malformed URL exception for file: {}", fileName, ex);
            throw new IOException("Invalid file path: " + fileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        Path filePath;
        
        if (fileName.startsWith("audio/")) {
            String actualFileName = fileName.substring("audio/".length());
            filePath = this.audioStorageLocation.resolve(actualFileName).normalize();
        } else if (fileName.startsWith("covers/")) {
            String actualFileName = fileName.substring("covers/".length());
            filePath = this.coverStorageLocation.resolve(actualFileName).normalize();
        } else {
            // Пробуем удалить из обеих директорий
            Path audioPath = this.audioStorageLocation.resolve(fileName).normalize();
            Path coverPath = this.coverStorageLocation.resolve(fileName).normalize();
            
            if (Files.exists(audioPath)) {
                filePath = audioPath;
            } else if (Files.exists(coverPath)) {
                filePath = coverPath;
            } else {
                log.warn("File not found for deletion: {}", fileName);
                return;
            }
        }
        
        // Проверка безопасности
        if (filePath.startsWith(this.audioStorageLocation) || filePath.startsWith(this.coverStorageLocation)) {
            Files.deleteIfExists(filePath);
            log.debug("File deleted: {}", filePath);
        } else {
            throw new IOException("Invalid file path for deletion: " + fileName);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            Path filePath;
            
            if (fileName.startsWith("audio/")) {
                String actualFileName = fileName.substring("audio/".length());
                filePath = this.audioStorageLocation.resolve(actualFileName).normalize();
            } else if (fileName.startsWith("covers/")) {
                String actualFileName = fileName.substring("covers/".length());
                filePath = this.coverStorageLocation.resolve(actualFileName).normalize();
            } else {
                Path audioPath = this.audioStorageLocation.resolve(fileName).normalize();
                Path coverPath = this.coverStorageLocation.resolve(fileName).normalize();
                return Files.exists(audioPath) || Files.exists(coverPath);
            }
            
            return Files.exists(filePath);
        } catch (Exception e) {
            log.error("Error checking file existence: {}", fileName, e);
            return false;
        }
    }

    @Override
    public Path getAudioDirectory() {
        return audioStorageLocation;
    }

    @Override
    public Path getCoverDirectory() {
        return coverStorageLocation;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}

