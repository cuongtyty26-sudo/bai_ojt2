package com.example.baiojt2.repository;

import com.example.baiojt2.dto.AdminUserRow;
import com.example.baiojt2.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRoleAndLockedFalse(String role);

    @Query(value = """
        select new com.example.baiojt2.dto.AdminUserRow(
            u.id, u.email, coalesce(p.fullName, c.name, ''), u.role, u.accountStatus, u.locked)
        from UserAccount u
        left join StudentProfile p on p.userId = u.id
        left join Company c on c.id = u.companyId
        where (:keyword = '' or lower(u.email) like lower(concat('%', :keyword, '%'))
            or lower(coalesce(p.fullName, c.name, '')) like lower(concat('%', :keyword, '%')))
          and (:role = 'ALL' or u.role = :role)
          and (:status = 'ALL' or (:status = 'LOCKED' and u.locked = true)
            or (:status = 'UNLOCKED' and u.locked = false) or u.accountStatus = :status)
        """,
        countQuery = """
        select count(u) from UserAccount u
        left join StudentProfile p on p.userId = u.id
        left join Company c on c.id = u.companyId
        where (:keyword = '' or lower(u.email) like lower(concat('%', :keyword, '%'))
            or lower(coalesce(p.fullName, c.name, '')) like lower(concat('%', :keyword, '%')))
          and (:role = 'ALL' or u.role = :role)
          and (:status = 'ALL' or (:status = 'LOCKED' and u.locked = true)
            or (:status = 'UNLOCKED' and u.locked = false) or u.accountStatus = :status)
        """)
    Page<AdminUserRow> searchForAdmin(@Param("keyword") String keyword, @Param("role") String role,
                                      @Param("status") String status, Pageable pageable);
}
