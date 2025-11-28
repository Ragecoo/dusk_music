package io.github.ragecoo.dusk_music.controller;

import io.github.ragecoo.dusk_music.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "Контроллер для работы с файлами (аудио и обложки)")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/audio/**")
    @Operation(summary = "Получить аудиофайл")
    public ResponseEntity<?> getAudioFile(HttpServletRequest request) {
        return getFile(request, "audio");
    }

    @GetMapping("/covers/**")
    @Operation(summary = "Получить обложку")
    public ResponseEntity<?> getCoverFile(HttpServletRequest request) {
        return getFile(request, "covers");
    }
    
    /**
     * Простой endpoint для получения файла по имени
     * Используется как fallback для совместимости
     */
    @GetMapping("/{fileType}/{fileName:.+}")
    @Operation(summary = "Получить файл по имени")
    public ResponseEntity<?> getFileByName(
            @PathVariable String fileType,
            @PathVariable String fileName) {
        try {
            String filePath = fileType + "/" + fileName;
            log.info("Getting file: {}", filePath);
            Resource resource = fileStorageService.loadFileAsResource(filePath);
            
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            String contentType = fileType.equals("audio") ? "audio/mpeg" : "image/jpeg";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.setAccessControlAllowOrigin("*");
            
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            log.error("Error getting file: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> getFile(HttpServletRequest request, String fileType) {
        try {
            // Извлекаем путь из URL
            String requestPath = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullUrl = requestPath + (queryString != null ? "?" + queryString : "");
            
            log.info("=== File request received ===");
            log.info("Request path: {}", requestPath);
            log.info("Full URL: {}", fullUrl);
            log.info("Request method: {}", request.getMethod());
            log.info("File type: {}", fileType);
            log.info("Remote address: {}", request.getRemoteAddr());
            log.info("Authorization header present: {}", request.getHeader("Authorization") != null);
            
            // Логируем заголовки авторизации (без токена)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                log.info("Authorization header type: {}", authHeader.startsWith("Bearer ") ? "Bearer" : "Other");
                log.info("Authorization header length: {}", authHeader.length());
            } else {
                log.warn("No Authorization header in request!");
            }
            
            String filePath = extractFilePath(requestPath, fileType);
            log.info("Extracted file path: {}", filePath);
            
            log.info("Attempting to load file resource...");
            Resource resource = fileStorageService.loadFileAsResource(filePath);
            log.info("File resource loaded from service");
            
            // Проверяем существование файла
            if (!resource.exists() || !resource.isReadable()) {
                log.error("File not found or not readable. Request path: {}, Extracted path: {}", requestPath, filePath);
                log.error("Resource exists: {}, isReadable: {}", resource.exists(), resource.isReadable());
                return ResponseEntity.notFound().build();
            }
            
            log.info("File found and readable: {}", filePath);
            
            // Определяем Content-Type
            String contentType = null;
            try {
                if (resource.isFile() && resource.getFile() != null) {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                }
            } catch (IOException ex) {
                log.warn("Could not determine file type for: {}", filePath);
            }

            // Fallback для Content-Type
            if (contentType == null) {
                if (fileType.equals("audio")) {
                    contentType = "audio/mpeg";
                } else {
                    contentType = "image/jpeg";
                }
            }

            // Получаем длину файла
            long contentLength;
            try {
                contentLength = resource.contentLength();
            } catch (IOException e) {
                log.warn("Could not determine content length for: {}", filePath);
                contentLength = -1;
            }

            // Проверяем Range заголовок для поддержки стриминга
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);
            log.info("Range header: {}", rangeHeader);
            
            if (rangeHeader != null && rangeHeader.startsWith("bytes=") && contentLength > 0 && fileType.equals("audio")) {
                log.info("Handling range request for audio file");
                return handleRangeRequest(resource, rangeHeader, contentType, contentLength, filePath);
            }

            // Если Range не указан, возвращаем весь файл
            log.info("Returning full file. Content-Type: {}, Content-Length: {}", contentType, contentLength);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(contentLength);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.setContentDispositionFormData("inline", resource.getFilename() != null ? resource.getFilename() : "file");
            
            // Добавляем CORS заголовки для поддержки запросов из браузера
            headers.setAccessControlAllowOrigin("*");
            headers.setAccessControlAllowMethods(java.util.List.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS));
            headers.setAccessControlAllowHeaders(java.util.List.of("*"));
            headers.setAccessControlExposeHeaders(java.util.List.of("Content-Length", "Content-Range", "Accept-Ranges"));

            log.info("Sending file response with status OK");
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("=== Error loading file ===");
            log.error("Error message: {}", e.getMessage());
            log.error("Error class: {}", e.getClass().getName());
            log.error("Request path: {}", request.getRequestURI());
            log.error("Stack trace:", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("=== Unexpected error loading file ===");
            log.error("Error message: {}", e.getMessage());
            log.error("Error class: {}", e.getClass().getName());
            log.error("Request path: {}", request.getRequestURI());
            log.error("Stack trace:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Обрабатывает HTTP Range запрос для поддержки стриминга аудио
     */
    private ResponseEntity<?> handleRangeRequest(Resource resource, String rangeHeader,
                                                 String contentType, long contentLength, String filePath) {
        try {
            // Парсим Range заголовок
            String rangeValue = rangeHeader.substring(6); // Убираем "bytes="
            
            final long rangeStart;
            final long rangeEnd;
            
            if (rangeValue.contains("-")) {
                String[] ranges = rangeValue.split("-");
                if (ranges.length == 2) {
                    if (!ranges[0].isEmpty() && !ranges[1].isEmpty()) {
                        // bytes=start-end
                        rangeStart = Long.parseLong(ranges[0]);
                        rangeEnd = Long.parseLong(ranges[1]);
                    } else if (!ranges[0].isEmpty()) {
                        // bytes=start- (от start до конца)
                        rangeStart = Long.parseLong(ranges[0]);
                        rangeEnd = contentLength - 1;
                    } else {
                        // bytes=-suffix (последние N байт)
                        long suffixLength = Long.parseLong(ranges[1]);
                        rangeStart = Math.max(0, contentLength - suffixLength);
                        rangeEnd = contentLength - 1;
                    }
                } else {
                    rangeStart = 0;
                    rangeEnd = contentLength - 1;
                }
            } else {
                rangeStart = 0;
                rangeEnd = contentLength - 1;
            }
            
            // Валидация диапазона
            if (rangeStart > rangeEnd || rangeStart < 0 || rangeEnd >= contentLength) {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_RANGE, "bytes */" + contentLength);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .headers(headers)
                        .build();
            }
            
            final long rangeLength = rangeEnd - rangeStart + 1;
            
            // Создаем StreamingResponseBody для частичного контента
            StreamingResponseBody streamingBody = outputStream -> {
                try (InputStream inputStream = resource.getInputStream()) {
                    // Пропускаем байты до rangeStart
                    long skipped = inputStream.skip(rangeStart);
                    if (skipped < rangeStart) {
                        throw new IOException("Could not skip to range start");
                    }
                    
                    // Читаем и записываем только нужный диапазон
                    byte[] buffer = new byte[8192];
                    long bytesToRead = rangeLength;
                    int bytesRead;
                    
                    while (bytesToRead > 0 && (bytesRead = inputStream.read(buffer, 0, 
                            (int) Math.min(buffer.length, bytesToRead))) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        bytesToRead -= bytesRead;
                    }
                }
            };
            
            // Настраиваем заголовки для частичного контента
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(rangeLength);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, contentLength));
            
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(streamingBody);
                    
        } catch (Exception e) {
            log.error("Error handling range request for: {}", filePath, e);
            // В случае ошибки возвращаем весь файл
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(contentLength);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(resource);
        }
    }

    private String extractFilePath(String requestPath, String fileType) {
        // Убираем префикс /api/v1/files/audio/ или /api/v1/files/covers/
        String prefix = "/api/v1/files/" + fileType + "/";
        log.info("Extracting file path. Request path: {}, Expected prefix: {}", requestPath, prefix);
        
        if (requestPath.startsWith(prefix)) {
            String fileName = requestPath.substring(prefix.length());
            log.info("Extracted fileName from {}: {}", requestPath, fileName);
            String result = fileType + "/" + fileName;
            log.info("Final file path for service: {}", result);
            return result;
        }
        log.warn("Request path {} does not start with expected prefix {}", requestPath, prefix);
        log.warn("Returning request path as-is: {}", requestPath);
        return requestPath;
    }
}

