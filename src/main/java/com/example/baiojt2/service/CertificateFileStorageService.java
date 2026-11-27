package com.example.baiojt2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Locale;
import java.util.UUID;

@Service
public class CertificateFileStorageService {
    public static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private final Path storageDirectory;

    public CertificateFileStorageService(
            @Value("${app.upload.certificates.directory:./uploads/certificates}") String directory) {
        this.storageDirectory = Paths.get(directory).toAbsolutePath().normalize();
    }

    public StoredCertificateFile store(MultipartFile file) {
        validate(file);
        String extension = extensionOf(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            Files.createDirectories(storageDirectory);
            Path destination = storageDirectory.resolve(storedName).normalize();
            if (!destination.getParent().equals(storageDirectory)) {
                throw new IllegalArgumentException("Tên file không hợp lệ.");
            }
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredCertificateFile(file.getOriginalFilename(), storedName,
                    file.getContentType(), file.getSize());
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể lưu file minh chứng.", ex);
        }
    }

    public Path load(String storedName) {
        if (storedName == null || !storedName.matches("[a-f0-9]{32}\\.(pdf|jpg|jpeg|png)")) {
            throw new IllegalArgumentException("Tên file lưu trữ không hợp lệ.");
        }
        Path file = storageDirectory.resolve(storedName).normalize();
        if (!file.getParent().equals(storageDirectory) || !Files.isRegularFile(file)) {
            throw new IllegalStateException("Không tìm thấy file minh chứng.");
        }
        return file;
    }

    public void delete(String storedName) {
        if (storedName == null) return;
        try {
            Files.deleteIfExists(load(storedName));
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể xóa file minh chứng.", ex);
        }
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tải lên file minh chứng.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File minh chứng không được vượt quá 5 MB.");
        }
        String extension = extensionOf(file.getOriginalFilename());
        String expectedType = switch (extension) {
            case ".pdf" -> "application/pdf";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            default -> throw new IllegalArgumentException("Chỉ chấp nhận file PDF, JPG hoặc PNG.");
        };
        if (!expectedType.equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Content-Type không khớp với phần mở rộng file.");
        }
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(8);
            boolean signatureValid = switch (extension) {
                case ".pdf" -> header.length >= 5 && header[0] == '%' && header[1] == 'P'
                        && header[2] == 'D' && header[3] == 'F' && header[4] == '-';
                case ".jpg", ".jpeg" -> header.length >= 3 && (header[0] & 0xff) == 0xff
                        && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
                case ".png" -> header.length >= 8 && (header[0] & 0xff) == 0x89
                        && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                        && header[4] == 0x0d && header[5] == 0x0a && header[6] == 0x1a && header[7] == 0x0a;
                default -> false;
            };
            if (!signatureValid) throw new IllegalArgumentException("Nội dung file không đúng định dạng đã khai báo.");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Không thể đọc file minh chứng.", ex);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Tên file không hợp lệ.");
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        int dot = lower.lastIndexOf('.');
        if (dot < 0) throw new IllegalArgumentException("File phải có phần mở rộng PDF, JPG hoặc PNG.");
        return lower.substring(dot);
    }

    public record StoredCertificateFile(String originalName, String storedName, String contentType, long size) {}
}
