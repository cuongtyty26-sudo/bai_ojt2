package com.example.baiojt2.service;

import com.example.baiojt2.dto.SkillForm;
import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillCategory;
import com.example.baiojt2.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private AdminSkillService skillService;

    @Test
    void detectsDuplicateNameWithoutCaseSensitivity() {
        when(skillRepository.existsByNameIgnoreCase("java")).thenReturn(true);

        assertTrue(skillService.isNameTaken(" java ", null));
        verify(skillRepository).existsByNameIgnoreCase("java");
    }

    @Test
    void allowsCurrentSkillToKeepItsOwnNameDuringEdit() {
        when(skillRepository.existsByNameIgnoreCaseAndIdNot("Java", 5L)).thenReturn(false);

        assertFalse(skillService.isNameTaken("Java", 5L));
    }

    @Test
    void createsSkillAfterTrimmingName() {
        SkillForm form = new SkillForm();
        form.setName("  Spring Boot  ");
        form.setCategory(SkillCategory.FRAMEWORK);

        skillService.create(form);

        verify(skillRepository).save(argThat(skill ->
                skill.getName().equals("Spring Boot")
                        && skill.getCategory() == SkillCategory.FRAMEWORK
                        && skill.isActive()));
    }

    @Test
    void hidesSkillWithoutDeletingIt() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.hide(1L);

        assertFalse(skill.isActive());
        verify(skillRepository, never()).delete(any());
    }

    @Test
    void preventsDeletingSkillUsedByStudents() {
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.countStudentUsage(1L)).thenReturn(3L);

        assertFalse(skillService.deleteIfUnused(1L));
        verify(skillRepository, never()).delete(any());
    }

    @Test
    void deletesSkillOnlyWhenNoStudentUsesIt() {
        Skill skill = new Skill("Unused", SkillCategory.TOOL);
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill));
        when(skillRepository.countStudentUsage(2L)).thenReturn(0L);

        assertTrue(skillService.deleteIfUnused(2L));
        verify(skillRepository).delete(skill);
    }

    @Test
    void throwsNotFoundForUnknownSkill() {
        when(skillRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> skillService.hide(404L));
    }
}
