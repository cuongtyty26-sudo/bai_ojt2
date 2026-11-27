package com.example.baiojt2.service;

import com.example.baiojt2.dto.AdminUserRow;
import com.example.baiojt2.dto.CreatePartnerForm;
import com.example.baiojt2.entity.Company;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.CompanyRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AdminUserService {
    private final UserAccountRepository users;
    private final CompanyRepository companies;
    private final PasswordEncoder encoder;

    public AdminUserService(UserAccountRepository users, CompanyRepository companies, PasswordEncoder encoder) {
        this.users = users; this.companies = companies; this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserRow> search(String keyword, String role, String status, int page, int size) {
        String normalizedRole = role == null ? "ALL" : role.toUpperCase(Locale.ROOT);
        String safeRole = switch (normalizedRole) {
            case "ADMIN", "PARTNER", "STUDENT" -> normalizedRole;
            default -> "ALL";
        };
        String normalizedStatus = status == null ? "ALL" : status.toUpperCase(Locale.ROOT);
        String safeStatus = switch (normalizedStatus) {
            case "ACTIVE", "PENDING", "REJECTED", "SUSPENDED", "LOCKED", "UNLOCKED" -> normalizedStatus;
            default -> "ALL";
        };
        return users.searchForAdmin(keyword == null ? "" : keyword.trim(), safeRole, safeStatus,
                PageRequest.of(Math.max(0, page), Math.min(Math.max(size, 5), 50), Sort.by(Sort.Direction.DESC, "id")));
    }

    @Transactional
    public void createPartner(CreatePartnerForm form) {
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        String companyName = form.getCompanyName().trim();
        if (users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email này đã được sử dụng.");
        if (companies.existsByNameIgnoreCase(companyName)) throw new IllegalArgumentException("Tên công ty đã tồn tại.");
        Company company = companies.save(new Company(companyName, email));
        UserAccount partner = new UserAccount(email, encoder.encode(form.getPassword()), "PARTNER", false, "ACTIVE");
        partner.setCompanyId(company.getId());
        users.save(partner);
    }

    @Transactional
    public void setLocked(Long targetId, boolean locked, Long currentAdminId) {
        UserAccount target = users.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
        if (locked && target.getId().equals(currentAdminId))
            throw new IllegalArgumentException("Bạn không thể tự khóa tài khoản đang đăng nhập.");
        if ("ADMIN".equals(target.getRole()) && locked) {
            long activeAdmins = users.countByRoleAndLockedFalse("ADMIN");
            if (activeAdmins <= 1) throw new IllegalArgumentException("Không thể khóa tài khoản ADMIN cuối cùng đang hoạt động.");
        }
        target.setLocked(locked);
    }
}
