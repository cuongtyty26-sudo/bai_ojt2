package com.example.baiojt2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePartnerForm {
    @NotBlank(message = "Vui lòng nhập tên công ty.")
    @Size(max = 150, message = "Tên công ty tối đa 150 ký tự.")
    private String companyName;
    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 255, message = "Email tối đa 255 ký tự.")
    private String email;
    @NotBlank(message = "Vui lòng nhập mật khẩu khởi tạo.")
    @Size(min = 8, max = 72, message = "Mật khẩu phải từ 8 đến 72 ký tự.")
    private String password;
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public void clearPassword() { password = null; }
}
