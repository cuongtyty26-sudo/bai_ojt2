package com.example.baiojt2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class AvatarFileStorageService {
    public static final long MAX_FILE_SIZE = 2L * 1024 * 1024;
    private final Path storageDirectory;

    public AvatarFileStorageService(
            @Value("${app.upload.avatars.directory:./uploads/avatars}") String directory) {
        this.storageDirectory = Paths.get(directory).toAbsolutePath().normalize();
    }

    public StoredAvatar store(MultipartFile file) {
        validate(file);
        String extension = extensionOf(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            Files.createDirectories(storageDirectory);
            Path destination = storageDirectory.resolve(storedName).normalize();
            if (!destination.getParent().equals(storageDirectory)) {
                throw new IllegalArgumentException("Tên ảnh không hợp lệ.");
            }
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredAvatar(storedName, file.getContentType());
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể lưu ảnh đại diện.", ex);
        }
    }

    public Path load(String storedName) {
        if (storedName == null || !storedName.matches("[a-f0-9]{32}\\.(jpg|jpeg|png)")) {
            throw new IllegalArgumentException("Tên ảnh lưu trữ không hợp lệ.");
        }
        Path file = storageDirectory.resolve(storedName).normalize();
        if (!file.getParent().equals(storageDirectory) || !Files.isRegularFile(file)) {
            throw new IllegalStateException("Không tìm thấy ảnh đại diện.");
        }
        return file;
    }

    public void delete(String storedName) {
        if (storedName == null) return;
        try {
            Path file = storageDirectory.resolve(storedName).normalize();
            if (!file.getParent().equals(storageDirectory)) {
                throw new IllegalArgumentException("Tên ảnh lưu trữ không hợp lệ.");
            }
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể xóa ảnh đại diện cũ.", ex);
        }
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ảnh đại diện.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Ảnh đại diện không được vượt quá 2 MB.");
        }
        String extension = extensionOf(file.getOriginalFilename());
        String expectedType = switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            default -> throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG hoặc PNG.");
        };
        if (!expectedType.equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Content-Type không khớp với phần mở rộng ảnh.");
        }
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(8);
            boolean valid = switch (extension) {
                case ".jpg", ".jpeg" -> header.length >= 3 && (header[0] & 0xff) == 0xff
                        && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
                case ".png" -> header.length >= 8 && (header[0] & 0xff) == 0x89
                        && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                        && header[4] == 0x0d && header[5] == 0x0a && header[6] == 0x1a && header[7] == 0x0a;
                default -> false;
            };
            if (!valid) throw new IllegalArgumentException("Nội dung file không đúng định dạng ảnh đã khai báo.");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Không thể đọc ảnh đại diện.", ex);
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Tên ảnh không hợp lệ.");
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        int dot = lower.lastIndexOf('.');
        if (dot < 0) throw new IllegalArgumentException("Ảnh phải có phần mở rộng JPG, JPEG hoặc PNG.");
        return lower.substring(dot);
    }

    public record StoredAvatar(String storedName, String contentType) {}
}
