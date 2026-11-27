package com.example.baiojt2.service;

import com.example.baiojt2.dto.EducationForm;
import com.example.baiojt2.entity.Education;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.EducationRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private StudentProfileRepository profileRepository;

    @InjectMocks
    private EducationService educationService;

    @Test
    void createsEducationForAuthenticatedStudentsOwnProfile() {
        StudentProfile profile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(profile, "id", 100L);
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(profile));

        EducationForm form = new EducationForm();
        form.setInstitution("University");
        form.setMajor("Computer Science");
        form.setStartDate(LocalDate.of(2020, 4, 1));
        form.setEndDate(LocalDate.of(2024, 3, 31));
        form.setGpa(new BigDecimal("3.50"));

        educationService.createEducation(10L, form);

        verify(educationRepository).save(org.mockito.ArgumentMatchers.argThat(education ->
                education.getProfileId().equals(100L)
                        && education.getInstitution().equals("University")
                        && education.getGpa().compareTo(new BigDecimal("3.50")) == 0));
    }

    @Test
    void blocksEditingEducationOwnedByAnotherStudent() {
        StudentProfile ownProfile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(ownProfile, "id", 100L);
        Education someoneElsesEducation = new Education(200L, "Other University", "IT",
                LocalDate.of(2019, 4, 1), null, null);
        when(educationRepository.findById(55L)).thenReturn(Optional.of(someoneElsesEducation));
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(ownProfile));

        assertThrows(AccessDeniedException.class,
                () -> educationService.getMyEducation(10L, 55L));
    }

    @Test
    void blocksDeletingEducationOwnedByAnotherStudent() {
        StudentProfile ownProfile = new StudentProfile(10L, "Student A");
        ReflectionTestUtils.setField(ownProfile, "id", 100L);
        Education someoneElsesEducation = new Education(200L, "Other University", "IT",
                LocalDate.of(2019, 4, 1), null, null);
        when(educationRepository.findById(56L)).thenReturn(Optional.of(someoneElsesEducation));
        when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(ownProfile));

        assertThrows(AccessDeniedException.class,
                () -> educationService.deleteEducation(10L, 56L));
    }

    @Test
    void returnsNotFoundForUnknownEducationId() {
        when(educationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> educationService.getMyEducation(10L, 999L));
    }

    @Test
    void acceptsDateRangeWhenStartIsNotAfterEnd() {
        EducationForm form = new EducationForm();
        form.setStartDate(LocalDate.of(2020, 4, 1));
        form.setEndDate(LocalDate.of(2024, 3, 31));

        org.junit.jupiter.api.Assertions.assertTrue(form.isDateRangeValid());
    }

    @Test
    void rejectsDateRangeWhenStartIsAfterEnd() {
        EducationForm form = new EducationForm();
        form.setStartDate(LocalDate.of(2024, 4, 1));
        form.setEndDate(LocalDate.of(2020, 3, 31));

        org.junit.jupiter.api.Assertions.assertFalse(form.isDateRangeValid());
    }
}
