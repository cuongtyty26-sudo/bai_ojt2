package com.example.baiojt2.repository;

import com.example.baiojt2.entity.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {

    List<ProfileSkill> findAllByProfile_IdOrderBySkill_CategoryAscSkill_NameAsc(Long profileId);

    Optional<ProfileSkill> findByProfile_IdAndSkill_Id(Long profileId, Long skillId);
}
