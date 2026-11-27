package com.example.baiojt2.repository;

import com.example.baiojt2.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends JpaRepository<StudentProject, Long> {

    List<StudentProject> findAllByProfile_IdOrderByIdDesc(Long profileId);

    Optional<StudentProject> findByIdAndProfile_Id(Long projectId, Long profileId);

    long countByProfile_Id(Long profileId);
}
