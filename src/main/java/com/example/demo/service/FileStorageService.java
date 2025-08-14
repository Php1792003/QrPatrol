// src/main/java/com/example/demo/service/FileStorageService.java
package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public String storeFile(MultipartFile file, String newFileName, String uploadDir) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadPath);

            if (file == null || file.isEmpty() || newFileName.contains("..")) {
                throw new RuntimeException("File không hợp lệ.");
            }

            Path targetLocation = uploadPath.resolve(newFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + newFileName, ex);
        }
    }
    // === THÊM PHƯƠNG THỨC MỚI VÀO ĐÂY ===
    /**
     * Xóa một file khỏi hệ thống.
     * @param fileName Tên của file cần xóa.
     * @param uploadDir Thư mục chứa file đó.
     */
    public void deleteFile(String fileName, String uploadDir) {
        if (fileName == null || fileName.isBlank()) {
            return; // Không làm gì nếu tên file rỗng
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName).normalize();

            // Kiểm tra xem file có thực sự tồn tại không trước khi xóa
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Đã xóa file thành công: {}", filePath);
            } else {
                logger.warn("File không tồn tại, không thể xóa: {}", filePath);
            }
        } catch (IOException ex) {
            logger.error("Không thể xóa file: {}", fileName, ex);

        }
    }
}