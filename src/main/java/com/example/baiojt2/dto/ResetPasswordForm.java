package com.example.baiojt2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordForm {
    @NotBlank(message = "Vui lòng nhập email.")
    private String email;
    @NotBlank(message = "Vui lòng nhập mã OTP.")
    @Pattern(regexp = "\\d{6}", message = "OTP phải gồm đúng 6 chữ số.")
    private String otp;
    @NotBlank(message = "Vui lòng nhập mật khẩu mới.")
    @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự.")
    private String newPassword;
    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới.")
    private String confirmPassword;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void clearPasswords() { newPassword = null; confirmPassword = null; otp = null; }
}
