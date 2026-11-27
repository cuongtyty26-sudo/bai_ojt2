package com.example.baiojt2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 150)
    private String name;
    @Column(length = 255)
    private String website;
    @Column(length = 255)
    private String address;
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    protected Company() {}
    public Company(String name, String contactEmail) {
        this.name = name; this.contactEmail = contactEmail;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}
