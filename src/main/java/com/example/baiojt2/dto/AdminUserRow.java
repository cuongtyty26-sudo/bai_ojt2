package com.example.baiojt2.dto;

public record AdminUserRow(Long id, String email, String displayName, String role,
                           String accountStatus, boolean locked) {}
