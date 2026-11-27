package com.example.baiojt2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CertificateFileStorageServiceTest {

    @TempDir
    Path tempDirectory;

    private CertificateFileStorageService storage() {
        return new CertificateFileStorageService(tempDirectory.toString());
    }

    @Test
    void storesPdfWithRandomNameOutsideOriginalFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "my-certificate.pdf",
                "application/pdf", "%PDF-1.7 sample".getBytes());

        CertificateFileStorageService.StoredCertificateFile stored = storage().store(file);

        assertNotEquals("my-certificate.pdf", stored.storedName());
        assertTrue(stored.storedName().matches("[a-f0-9]{32}\\.pdf"));
        assertTrue(Files.isRegularFile(storage().load(stored.storedName())));
        assertEquals("my-certificate.pdf", stored.originalName());
    }

    @Test
    void rejectsUnsupportedExtension() {
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "malware.exe",
                "application/pdf", "%PDF-1.7".getBytes());

        assertThrows(IllegalArgumentException.class, () -> storage().validate(file));
    }

    @Test
    void rejectsMismatchedContentType() {
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "image.png",
                "application/pdf", new byte[]{(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10});

        assertThrows(IllegalArgumentException.class, () -> storage().validate(file));
    }

    @Test
    void rejectsFakePdfContent() {
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "fake.pdf",
                "application/pdf", "not a pdf".getBytes());

        assertThrows(IllegalArgumentException.class, () -> storage().validate(file));
    }

    @Test
    void rejectsFilesLargerThanFiveMegabytes() {
        byte[] tooLarge = new byte[(int) CertificateFileStorageService.MAX_FILE_SIZE + 1];
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "large.pdf",
                "application/pdf", tooLarge);

        assertThrows(IllegalArgumentException.class, () -> storage().validate(file));
    }

    @Test
    void deletesStoredFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("evidenceFile", "proof.pdf",
                "application/pdf", "%PDF-1.7 sample".getBytes());
        CertificateFileStorageService service = storage();
        var stored = service.store(file);
        Path path = service.load(stored.storedName());

        service.delete(stored.storedName());

        assertFalse(Files.exists(path));
    }
}
