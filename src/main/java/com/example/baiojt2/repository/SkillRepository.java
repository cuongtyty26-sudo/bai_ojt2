package com.example.baiojt2.repository;

import com.example.baiojt2.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByCategoryAscNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query(value = "SELECT COUNT(*) FROM profile_skills WHERE skill_id = :skillId", nativeQuery = true)
    long countStudentUsage(@Param("skillId") Long skillId);
}
