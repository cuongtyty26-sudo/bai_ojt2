package com.example.baiojt2.service;

import com.example.baiojt2.dto.StudentProfileForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentProfileServiceTest {

    @Mock
    private StudentProfileRepository profileRepository;

    @InjectMocks
    private StudentProfileService profileService;

    @Test
    void updatesOnlyProfileOwnedByAuthenticatedUser() {
        StudentProfile profile = new StudentProfile(12L, "Nguyen Van A");
        when(profileRepository.findByUserId(12L)).thenReturn(Optional.of(profile));

        StudentProfileForm form = new StudentProfileForm();
        form.setFullName("Nguyen Van B");
        form.setDateOfBirth(LocalDate.of(2000, 1, 2));
        form.setPhone("+819012345678");
        form.setSummary("Java developer");
        form.setDesiredPosition("Backend Engineer");
        form.setDesiredWorkplace("Tokyo");

        profileService.updateMyProfile(12L, form);

        assertEquals("Nguyen Van B", profile.getFullName());
        assertEquals(LocalDate.of(2000, 1, 2), profile.getDateOfBirth());
        assertEquals("+819012345678", profile.getPhone());
        assertEquals("Java developer", profile.getSummary());
        assertEquals("Backend Engineer", profile.getDesiredPosition());
        assertEquals("Tokyo", profile.getDesiredWorkplace());
        verify(profileRepository).findByUserId(12L);
    }

    @Test
    void doesNotUpdateWhenAuthenticatedUserHasNoOwnProfile() {
        when(profileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> profileService.getMyProfile(99L));
    }
}
