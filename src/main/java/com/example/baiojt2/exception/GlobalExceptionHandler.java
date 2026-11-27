package com.example.baiojt2.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Converts application exceptions into friendly pages or one-time flash messages.
 * Internal exception details are deliberately not added to the response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException exception,
                                          RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException() {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.addObject("errorMessage", "Bạn không có quyền truy cập hoặc chỉnh sửa dữ liệu này.");
        return modelAndView;
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ModelAndView handleNotFoundException() {
        return errorPage("error/404", HttpStatus.NOT_FOUND,
                "Không tìm thấy dữ liệu bạn yêu cầu.");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException(Exception exception,
                                                  HttpServletRequest request) {
        // Keep the exception on the server logs; never expose it in the HTML response.
        request.getServletContext().log("Unexpected application error", exception);
        return errorPage("error/500", HttpStatus.INTERNAL_SERVER_ERROR,
                "Hệ thống đang gặp sự cố. Vui lòng thử lại sau.");
    }

    private ModelAndView errorPage(String viewName, HttpStatus status, String message) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.setStatus(status);
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }
}
