package com.example.baiojt2.dto;

import com.example.baiojt2.validation.ValidRegistration;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@ValidRegistration
public class RegistrationForm {

    @NotBlank(message = "Vui lòng nhập họ và tên.")
    @Size(max = 150, message = "Họ và tên không được vượt quá 150 ký tự.")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự.")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự.")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu.")
    private String confirmPassword;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void clearPasswords() {
        this.password = null;
        this.confirmPassword = null;
    }
}
