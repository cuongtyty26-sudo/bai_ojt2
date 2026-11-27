package com.example.baiojt2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class StudentProjectForm {

    @NotBlank(message = "Vui lòng nhập tên dự án.")
    @Size(max = 200, message = "Tên dự án tối đa 200 ký tự.")
    private String name;

    @NotBlank(message = "Vui lòng nhập vai trò của bạn trong dự án.")
    @Size(max = 150, message = "Vai trò tối đa 150 ký tự.")
    private String role;

    @NotBlank(message = "Vui lòng nhập công nghệ đã sử dụng.")
    @Size(max = 500, message = "Danh sách công nghệ tối đa 500 ký tự.")
    private String technologies;

    @NotBlank(message = "Vui lòng nhập link GitHub.")
    @URL(protocol = "https", host = "github.com", message = "Vui lòng nhập URL HTTPS hợp lệ từ github.com.")
    @Size(max = 500, message = "Link GitHub tối đa 500 ký tự.")
    private String githubUrl;

    @URL(protocol = "https", message = "Link demo phải là URL HTTPS hợp lệ.")
    @Size(max = 500, message = "Link demo tối đa 500 ký tự.")
    private String demoUrl;

    @Size(max = 5000, message = "Mô tả tối đa 5000 ký tự.")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String demoUrl) { this.demoUrl = demoUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
