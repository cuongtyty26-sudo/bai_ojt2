package com.example.baiojt2.dto;

import com.example.baiojt2.entity.Education;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EducationForm {

    @NotBlank(message = "Vui lòng nhập tên trường.")
    @Size(max = 200, message = "Tên trường không được vượt quá 200 ký tự.")
    private String institution;

    @NotBlank(message = "Vui lòng nhập chuyên ngành.")
    @Size(max = 150, message = "Chuyên ngành không được vượt quá 150 ký tự.")
    private String major;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu.")
    private LocalDate startDate;

    private LocalDate endDate;

    @Digits(integer = 1, fraction = 2, message = "GPA chỉ được có tối đa 2 chữ số thập phân.")
    @DecimalMin(value = "0.0", message = "GPA phải từ 0 đến 4.")
    @DecimalMax(value = "4.0", message = "GPA phải từ 0 đến 4.")
    private BigDecimal gpa;

    @AssertTrue(message = "Ngày bắt đầu không được sau ngày kết thúc.")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }

    public static EducationForm from(Education education) {
        EducationForm form = new EducationForm();
        form.setInstitution(education.getInstitution());
        form.setMajor(education.getMajor());
        form.setStartDate(education.getStartDate());
        form.setEndDate(education.getEndDate());
        form.setGpa(education.getGpa());
        return form;
    }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getGpa() { return gpa; }
    public void setGpa(BigDecimal gpa) { this.gpa = gpa; }
}
