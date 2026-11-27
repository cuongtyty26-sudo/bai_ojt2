package com.example.baiojt2.repository;

import com.example.baiojt2.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByProfileIdOrderByStartDateDescIdDesc(Long profileId);
}
