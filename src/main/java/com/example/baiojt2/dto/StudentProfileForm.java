package com.example.baiojt2.dto;

import com.example.baiojt2.entity.StudentProfile;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class StudentProfileForm {

    @jakarta.validation.constraints.NotBlank(message = "Vui lòng nhập họ và tên.")
    @Size(max = 150, message = "Họ và tên không được vượt quá 150 ký tự.")
    private String fullName;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ.")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^$|^\\+?[0-9][0-9\\s().-]{7,19}$",
            message = "Số điện thoại không đúng định dạng.")
    private String phone;

    @Size(max = 2000, message = "Giới thiệu bản thân không được vượt quá 2000 ký tự.")
    private String summary;

    @Size(max = 200, message = "Vị trí mong muốn không được vượt quá 200 ký tự.")
    private String desiredPosition;

    @Size(max = 200, message = "Nơi làm việc mong muốn không được vượt quá 200 ký tự.")
    private String desiredWorkplace;

    public static StudentProfileForm from(StudentProfile profile) {
        StudentProfileForm form = new StudentProfileForm();
        form.setFullName(profile.getFullName());
        form.setDateOfBirth(profile.getDateOfBirth());
        form.setPhone(profile.getPhone());
        form.setSummary(profile.getSummary());
        form.setDesiredPosition(profile.getDesiredPosition());
        form.setDesiredWorkplace(profile.getDesiredWorkplace());
        return form;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDesiredPosition() { return desiredPosition; }
    public void setDesiredPosition(String desiredPosition) { this.desiredPosition = desiredPosition; }
    public String getDesiredWorkplace() { return desiredWorkplace; }
    public void setDesiredWorkplace(String desiredWorkplace) { this.desiredWorkplace = desiredWorkplace; }
}
