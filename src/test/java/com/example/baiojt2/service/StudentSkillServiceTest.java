package com.example.baiojt2.service;

import com.example.baiojt2.entity.ProfileSkill;
import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillCategory;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.ProfileSkillRepository;
import com.example.baiojt2.repository.SkillRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentSkillServiceTest {

    @Mock
    private StudentProfileRepository profileRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ProfileSkillRepository profileSkillRepository;
    @InjectMocks
    private StudentSkillService service;

    private StudentProfile profile;

    @BeforeEach
    void setUp() {
        profile = new StudentProfile(10L, "Student");
        ReflectionTestUtils.setField(profile, "id", 20L);
        lenient().when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(profile));
    }

    @Test
    void addsActiveSkillToOwnProfile() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        ReflectionTestUtils.setField(skill, "id", 5L);
        when(skillRepository.findById(5L)).thenReturn(Optional.of(skill));
        when(profileSkillRepository.findByProfile_IdAndSkill_Id(20L, 5L)).thenReturn(Optional.empty());

        service.addSkill(10L, 5L, 4);

        verify(profileSkillRepository).save(argThat(link ->
                link.getProfile().getId().equals(20L)
                        && link.getSkill().getId().equals(5L)
                        && link.getProficiencyLevel() == 4));
    }

    @Test
    void rejectsDuplicateSkill() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        when(skillRepository.findById(5L)).thenReturn(Optional.of(skill));
        when(profileSkillRepository.findByProfile_IdAndSkill_Id(20L, 5L))
                .thenReturn(Optional.of(new ProfileSkill(profile, skill, 3)));

        assertThrows(IllegalStateException.class, () -> service.addSkill(10L, 5L, 3));
        verify(profileSkillRepository, never()).save(any());
    }

    @Test
    void rejectsHiddenSkill() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        skill.setActive(false);
        when(skillRepository.findById(5L)).thenReturn(Optional.of(skill));

        assertThrows(IllegalStateException.class, () -> service.addSkill(10L, 5L, 2));
        verify(profileSkillRepository, never()).save(any());
    }

    @Test
    void rejectsLevelOutsideOneToFive() {
        assertThrows(IllegalArgumentException.class, () -> service.addSkill(10L, 5L, 6));
        verify(skillRepository, never()).findById(any());
    }

    @Test
    void updatesOnlySkillBelongingToOwnProfile() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        ProfileSkill link = new ProfileSkill(profile, skill, 2);
        when(profileSkillRepository.findByProfile_IdAndSkill_Id(20L, 5L)).thenReturn(Optional.of(link));

        service.updateLevel(10L, 5L, 5);

        assertEquals(5, link.getProficiencyLevel());
    }

    @Test
    void removesOnlySkillBelongingToOwnProfile() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        ProfileSkill link = new ProfileSkill(profile, skill, 2);
        when(profileSkillRepository.findByProfile_IdAndSkill_Id(20L, 5L)).thenReturn(Optional.of(link));

        service.removeSkill(10L, 5L);

        verify(profileSkillRepository).delete(link);
    }

    @Test
    void cannotRemoveAnotherProfileSkill() {
        when(profileSkillRepository.findByProfile_IdAndSkill_Id(20L, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.removeSkill(10L, 99L));
        verify(profileSkillRepository, never()).delete(any());
    }
}
