package io.github.ragecoo.dusk_music.util;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class AudioDurationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(AudioDurationExtractor.class);
    
    private static final String DEFAULT_AUDIO_DIR = "uploads/audio";
    private static final String DEFAULT_COVER_DIR = "uploads/covers";

    /**
     * Извлекает длительность аудиофайла в секундах
     * @param audioUrl URL или путь к аудиофайлу
     * @return длительность в секундах, или null если не удалось определить
     */
    public Integer extractDuration(String audioUrl) {
        if (audioUrl == null || audioUrl.isBlank()) {
            logger.warn("Audio URL is null or empty");
            return null;
        }

        File audioFile = null;
        try {
            audioFile = getAudioFile(audioUrl);
            if (audioFile == null || !audioFile.exists()) {
                logger.warn("Audio file not found: {}", audioUrl);
                return null;
            }

            try (FileInputStream inputStream = new FileInputStream(audioFile)) {
                int duration = calculateDuration(inputStream, audioFile.length());
                
                logger.debug("Extracted duration {} seconds from file: {}", duration, audioUrl);
                return duration;
            }

        } catch (BitstreamException e) {
            logger.error("Bitstream error reading audio file: {}", audioUrl, e);
        } catch (IOException e) {
            logger.error("IO error reading audio file: {}", audioUrl, e);
        } catch (Exception e) {
            logger.error("Unexpected error extracting duration from: {}", audioUrl, e);
        }

        return null;
    }

    /**
     * Вычисляет длительность MP3 файла в секундах
     * @param inputStream поток для чтения файла
     * @param fileSize размер файла в байтах
     * @return длительность в секундах
     */
    private int calculateDuration(InputStream inputStream, long fileSize) throws BitstreamException, IOException {
        Bitstream bitstream = null;
        try {
            bitstream = new Bitstream(inputStream);
            Header header = bitstream.readFrame();
            


            int totalFrames = 0;
            int msPerFrame = (int) header.ms_per_frame();
            long totalMs = 0;

            // Читаем все фреймы для точного подсчета
            while (header != null) {
                totalFrames++;
                totalMs += msPerFrame;
                bitstream.closeFrame();
                header = bitstream.readFrame();
            }

            // Если не удалось прочитать фреймы, пытаемся оценить по размеру файла
            if (totalFrames == 0) {
                logger.warn("Could not read frames, estimating duration from file size");
                // Примерная оценка: средний битрейт 128 kbps
                // Длительность (сек) = (размер файла в байтах * 8) / (битрейт в битах/сек)
                int estimatedBitrate = 128000; // 128 kbps
                totalMs = (fileSize * 8 * 1000) / estimatedBitrate;
            }

            return (int) (totalMs / 1000);
        } finally {
            if (bitstream != null) {
                try {
                    bitstream.close();
                } catch (Exception e) {
                    logger.warn("Error closing bitstream", e);
                }
            }
        }
    }

    /**
     * Получает File объект из URL или пути к файлу
     * @param audioUrl URL или путь к файлу
     * @return File объект или null если не удалось создать
     */
    private File getAudioFile(String audioUrl) throws IOException {
        // Если это URL (http/https)
        if (audioUrl.startsWith("http://") || audioUrl.startsWith("https://")) {
            return downloadTemporaryFile(audioUrl);
        }
        
        // Если путь начинается с "audio/" или "covers/", ищем в соответствующих директориях
        if (audioUrl.startsWith("audio/")) {
            String fileName = audioUrl.substring("audio/".length());
            Path audioPath = Paths.get(DEFAULT_AUDIO_DIR, fileName).toAbsolutePath().normalize();
            if (Files.exists(audioPath)) {
                return audioPath.toFile();
            }
            // Также пробуем относительный путь
            audioPath = Paths.get(DEFAULT_AUDIO_DIR, fileName).normalize();
            if (Files.exists(audioPath)) {
                return audioPath.toFile();
            }
        }
        
        if (audioUrl.startsWith("covers/")) {
            String fileName = audioUrl.substring("covers/".length());
            Path coverPath = Paths.get(DEFAULT_COVER_DIR, fileName).toAbsolutePath().normalize();
            if (Files.exists(coverPath)) {
                return coverPath.toFile();
            }
            // Также пробуем относительный путь
            coverPath = Paths.get(DEFAULT_COVER_DIR, fileName).normalize();
            if (Files.exists(coverPath)) {
                return coverPath.toFile();
            }
        }
        
        // Если это локальный путь к файлу (абсолютный)
        File file = new File(audioUrl);
        if (file.exists()) {
            return file;
        }

        // Попытка как относительный путь
        Path path = Path.of(audioUrl);
        if (Files.exists(path)) {
            return path.toFile();
        }

        return null;
    }

    /**
     * Скачивает файл по URL во временный файл для обработки
     * @param url URL файла
     * @return временный файл
     */
    private File downloadTemporaryFile(String url) throws IOException {
        try {
            URL fileUrl = new URL(url);
            Path tempFile = Files.createTempFile("audio_", ".mp3");
            Files.copy(fileUrl.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            
            File file = tempFile.toFile();
            file.deleteOnExit(); // Удалить после завершения работы
            
            return file;
        } catch (Exception e) {
            logger.error("Error downloading file from URL: {}", url, e);
            throw new IOException("Failed to download audio file from URL: " + url, e);
        }
    }
}
