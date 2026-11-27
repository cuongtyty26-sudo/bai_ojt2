package com.example.baiojt2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoleHomeController {

    @GetMapping("/admin/")
    public String adminHome() {
        return "role-home";
    }

    @GetMapping("/partner/")
    public String partnerHome() {
        return "role-home";
    }

    @GetMapping("/me/")
    public String studentHome() {
        return "role-home";
    }
}
