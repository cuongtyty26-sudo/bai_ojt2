package com.example.baiojt2.service;

import com.example.baiojt2.dto.StudentProjectForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.entity.StudentProject;
import com.example.baiojt2.repository.StudentProfileRepository;
import com.example.baiojt2.repository.StudentProjectRepository;
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
class StudentProjectServiceTest {

    @Mock
    private StudentProfileRepository profileRepository;
    @Mock
    private StudentProjectRepository projectRepository;
    @InjectMocks
    private StudentProjectService service;

    private StudentProfile profile;

    @BeforeEach
    void setUp() {
        profile = new StudentProfile(10L, "Student");
        ReflectionTestUtils.setField(profile, "id", 20L);
        lenient().when(profileRepository.findByUserId(10L)).thenReturn(Optional.of(profile));
    }

    private StudentProjectForm validForm() {
        StudentProjectForm form = new StudentProjectForm();
        form.setName("Portfolio App");
        form.setRole("Backend Developer");
        form.setTechnologies("Java, Spring Boot, MySQL");
        form.setGithubUrl("https://github.com/example/portfolio");
        form.setDemoUrl("https://example.com/demo");
        form.setDescription("A sample project");
        return form;
    }

    @Test
    void createsProjectWhenProfileHasRoom() {
        when(profileRepository.findByUserIdForUpdate(10L)).thenReturn(Optional.of(profile));
        when(projectRepository.countByProfile_Id(20L)).thenReturn(19L);

        service.create(10L, validForm());

        verify(projectRepository).save(argThat(project ->
                project.getProfile().getId().equals(20L)
                        && project.getName().equals("Portfolio App")
                        && project.getRole().equals("Backend Developer")
                        && project.getGithubUrl().equals("https://github.com/example/portfolio")));
    }

    @Test
    void rejectsTwentyFirstProject() {
        when(profileRepository.findByUserIdForUpdate(10L)).thenReturn(Optional.of(profile));
        when(projectRepository.countByProfile_Id(20L)).thenReturn(20L);

        assertThrows(IllegalStateException.class, () -> service.create(10L, validForm()));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void normalizesBlankOptionalLinksToNull() {
        StudentProjectForm form = validForm();
        form.setDemoUrl(" ");
        when(profileRepository.findByUserIdForUpdate(10L)).thenReturn(Optional.of(profile));
        when(projectRepository.countByProfile_Id(20L)).thenReturn(0L);

        service.create(10L, form);

        verify(projectRepository).save(argThat(project -> project.getDemoUrl() == null));
    }

    @Test
    void updatesOnlyProjectOwnedByCurrentStudent() {
        StudentProject project = new StudentProject(profile, "Old", "Developer",
                "Java", "https://github.com/example/old", null, "Old description");
        when(projectRepository.findByIdAndProfile_Id(5L, 20L)).thenReturn(Optional.of(project));

        StudentProjectForm form = validForm();
        service.update(10L, 5L, form);

        assertEquals("Portfolio App", project.getName());
        assertEquals("Backend Developer", project.getRole());
    }

    @Test
    void rejectsProjectNotOwnedByCurrentStudent() {
        when(projectRepository.findByIdAndProfile_Id(99L, 20L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.delete(10L, 99L));
        verify(projectRepository, never()).delete(any());
    }

    @Test
    void deletesOwnedProject() {
        StudentProject project = new StudentProject(profile, "Old", "Developer",
                "Java", "https://github.com/example/old", null, "Old description");
        when(projectRepository.findByIdAndProfile_Id(5L, 20L)).thenReturn(Optional.of(project));

        service.delete(10L, 5L);

        verify(projectRepository).delete(project);
    }
}
