package com.example.baiojt2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Friendly page used by Spring Security when an authenticated user lacks permission.
 */
@Controller
public class ErrorPageController {

    @GetMapping("/error/403")
    public ModelAndView forbidden() {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.addObject("errorMessage",
                "Bạn không có quyền truy cập chức năng này.");
        return modelAndView;
    }
}
