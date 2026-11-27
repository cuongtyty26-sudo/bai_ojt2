package com.example.baiojt2.service;

import com.example.baiojt2.dto.EducationForm;
import com.example.baiojt2.entity.Education;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.EducationRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {
    @Mock private EducationRepository educationRepository;
    @Mock private StudentProfileRepository profileRepository;
    @InjectMocks private EducationService educationService;

    private StudentProfile ownProfile;
    private EducationForm form;

    @BeforeEach
    void setUp() {
        ownProfile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(ownProfile, "id", 100L);
        lenient().when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(ownProfile));

        form = new EducationForm();
        form.setInstitution("  University  ");
        form.setMajor("  Computer Science  ");
        form.setStartDate(LocalDate.of(2020, 4, 1));
        form.setEndDate(LocalDate.of(2024, 3, 31));
        form.setGpa(new BigDecimal("3.50"));
    }

    @Test
    void createsEducationForAuthenticatedStudentsOwnProfile() {
        educationService.createEducation(10L, form);

        ArgumentCaptor<Education> captor = ArgumentCaptor.forClass(Education.class);
        verify(educationRepository).save(captor.capture());
        assertEquals(100L, captor.getValue().getProfileId());
        assertEquals("University", captor.getValue().getInstitution());
        assertEquals("Computer Science", captor.getValue().getMajor());
        assertEquals(new BigDecimal("3.50"), captor.getValue().getGpa());
    }

    @Test
    void listsOnlyTheAuthenticatedStudentsProfileEducation() {
        List<Education> expected = List.of(new Education(100L, "University", "IT",
                LocalDate.of(2020, 4, 1), null, null));
        when(educationRepository.findAllByProfileIdOrderByStartDateDescIdDesc(100L)).thenReturn(expected);

        assertEquals(expected, educationService.listMyEducations(10L));
        verify(educationRepository).findAllByProfileIdOrderByStartDateDescIdDesc(100L);
    }

    @Test
    void mapsOwnedEducationIntoEditForm() {
        Education education = new Education(100L, "University", "IT",
                LocalDate.of(2020, 4, 1), null, new BigDecimal("3.25"));
        when(educationRepository.findById(7L)).thenReturn(Optional.of(education));

        EducationForm result = educationService.getMyEducation(10L, 7L);

        assertEquals("University", result.getInstitution());
        assertEquals("IT", result.getMajor());
        assertEquals(new BigDecimal("3.25"), result.getGpa());
    }

    @Test
    void updatesOwnedEducationAndTrimsTextFields() {
        Education education = new Education(100L, "Old School", "Old Major",
                LocalDate.of(2018, 4, 1), null, null);
        when(educationRepository.findById(7L)).thenReturn(Optional.of(education));

        educationService.updateEducation(10L, 7L, form);

        assertEquals("University", education.getInstitution());
        assertEquals("Computer Science", education.getMajor());
        assertEquals(LocalDate.of(2024, 3, 31), education.getEndDate());
        assertEquals(new BigDecimal("3.50"), education.getGpa());
    }

    @Test
    void deletesOwnedEducation() {
        Education education = new Education(100L, "University", "IT",
                LocalDate.of(2020, 4, 1), null, null);
        when(educationRepository.findById(8L)).thenReturn(Optional.of(education));

        educationService.deleteEducation(10L, 8L);

        verify(educationRepository).delete(education);
    }

    @Test
    void blocksReadingEducationOwnedByAnotherStudent() {
        when(educationRepository.findById(9L)).thenReturn(Optional.of(new Education(200L,
                "Other University", "IT", LocalDate.of(2019, 4, 1), null, null)));

        assertThrows(AccessDeniedException.class, () -> educationService.getMyEducation(10L, 9L));
        verify(profileRepository).findByUserId(10L);
    }

    @Test
    void blocksDeletingEducationOwnedByAnotherStudent() {
        when(educationRepository.findById(9L)).thenReturn(Optional.of(new Education(200L,
                "Other University", "IT", LocalDate.of(2019, 4, 1), null, null)));

        assertThrows(AccessDeniedException.class, () -> educationService.deleteEducation(10L, 9L));
        verify(educationRepository, never()).delete(any());
    }

    @Test
    void returnsNotFoundForUnknownEducationId() {
        when(educationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> educationService.getMyEducation(10L, 999L));
        verify(profileRepository, never()).findByUserId(any());
    }

    @Test
    void rejectsOperationWhenStudentHasNoProfile() {
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> educationService.createEducation(10L, form));
        verify(educationRepository, never()).save(any());
    }
}
