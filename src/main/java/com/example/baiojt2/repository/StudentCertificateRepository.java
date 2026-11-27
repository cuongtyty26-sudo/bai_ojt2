package com.example.baiojt2.repository;

import com.example.baiojt2.entity.StudentCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentCertificateRepository extends JpaRepository<StudentCertificate, Long> {
    List<StudentCertificate> findAllByProfile_IdOrderByIssuedAtDescIdDesc(Long profileId);
    Optional<StudentCertificate> findByIdAndProfile_Id(Long certificateId, Long profileId);
}
