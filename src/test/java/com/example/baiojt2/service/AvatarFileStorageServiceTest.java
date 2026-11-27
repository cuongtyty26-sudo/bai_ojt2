package com.example.baiojt2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AvatarFileStorageServiceTest {
    @TempDir
    Path tempDir;

    private AvatarFileStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new AvatarFileStorageService(tempDir.toString());
    }

    @Test
    void storesValidPngWithRandomName() throws Exception {
        byte[] pngHeader = {(byte) 0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a, 1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.png",
                "image/png", pngHeader);

        AvatarFileStorageService.StoredAvatar stored = storageService.store(file);

        assertTrue(stored.storedName().matches("[a-f0-9]{32}\\.png"));
        assertEquals("image/png", stored.contentType());
        assertTrue(Files.isRegularFile(storageService.load(stored.storedName())));
        storageService.delete(stored.storedName());
        assertFalse(Files.exists(tempDir.resolve(stored.storedName())));
    }

    @Test
    void rejectsUnsupportedExtension() {
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.gif",
                "image/gif", new byte[]{1, 2, 3});
        assertThrows(IllegalArgumentException.class, () -> storageService.validate(file));
    }

    @Test
    void rejectsMimeTypeMismatch() {
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.png",
                "image/jpeg", new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a});
        assertThrows(IllegalArgumentException.class, () -> storageService.validate(file));
    }

    @Test
    void rejectsFakePngContent() {
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.png",
                "image/png", "not an image".getBytes());
        assertThrows(IllegalArgumentException.class, () -> storageService.validate(file));
    }

    @Test
    void rejectsFilesLargerThanTwoMiB() {
        byte[] tooLarge = new byte[(int) AvatarFileStorageService.MAX_FILE_SIZE + 1];
        MockMultipartFile file = new MockMultipartFile("avatarFile", "avatar.jpg",
                "image/jpeg", tooLarge);
        assertThrows(IllegalArgumentException.class, () -> storageService.validate(file));
    }
}
