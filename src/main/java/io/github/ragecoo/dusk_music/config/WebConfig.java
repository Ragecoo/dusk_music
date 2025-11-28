package io.github.ragecoo.dusk_music.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.storage.audio-dir:/opt/app/uploads/audio}")
    private String audioDir;

    @Value("${file.storage.cover-dir:/opt/app/uploads/covers}")
    private String coverDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Определяем базовый путь к uploads
        // В Docker это будет /opt/app/uploads, локально - uploads
        Path audioPath = Paths.get(audioDir);
        Path uploadsBasePath = audioPath.getParent(); // Получаем родительскую директорию (uploads)
        
        if (uploadsBasePath == null) {
            // Fallback: используем текущую директорию
            uploadsBasePath = Paths.get("uploads").toAbsolutePath().normalize();
        }
        
        String uploadPath = uploadsBasePath.toAbsolutePath().normalize().toString();
        
        log.info("Configuring static resources from: {}", uploadPath);
        
        // Регистрируем обработчик для статических файлов
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600); // Кэш на 1 час
        
        // Альтернативный путь для обратной совместимости
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600);
    }
}

