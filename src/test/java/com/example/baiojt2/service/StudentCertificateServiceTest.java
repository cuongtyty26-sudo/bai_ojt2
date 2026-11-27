package com.example.baiojt2.service;

import com.example.baiojt2.entity.StudentCertificate;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.StudentCertificateRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentCertificateServiceTest {

    @Mock
    private StudentProfileRepository profileRepository;
    @Mock
    private StudentCertificateRepository certificateRepository;
    @Mock
    private CertificateFileStorageService storageService;
    @InjectMocks
    private StudentCertificateService service;

    @Test
    void studentCannotLoadAnotherStudentsCertificate() {
        StudentProfile ownProfile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(ownProfile, "id", 100L);
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(ownProfile));
        when(certificateRepository.findByIdAndProfile_Id(500L, 100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.loadMine(10L, 500L));
        verifyNoInteractions(storageService);
    }

    @Test
    void studentCannotDeleteAnotherStudentsCertificate() {
        StudentProfile ownProfile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(ownProfile, "id", 100L);
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(ownProfile));
        when(certificateRepository.findByIdAndProfile_Id(500L, 100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteMine(10L, 500L));
        verify(certificateRepository, never()).delete(any());
        verifyNoInteractions(storageService);
    }

    @Test
    void adminCanLoadAnyExistingCertificate() {
        StudentProfile anotherProfile = new StudentProfile(20L, "Student B");
        ReflectionTestUtils.setField(anotherProfile, "id", 200L);
        StudentCertificate certificate = new StudentCertificate(anotherProfile, "AWS Cloud Practitioner",
                "Amazon", LocalDate.of(2025, 1, 1), "aws.pdf",
                "0123456789abcdef0123456789abcdef.pdf", "application/pdf", 100L);
        when(certificateRepository.findById(500L)).thenReturn(Optional.of(certificate));
        when(storageService.load("0123456789abcdef0123456789abcdef.pdf"))
                .thenReturn(java.nio.file.Path.of("/tmp/aws.pdf"));

        service.loadForAdmin(500L);

        verify(storageService).load("0123456789abcdef0123456789abcdef.pdf");
    }
}
