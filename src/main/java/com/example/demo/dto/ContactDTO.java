package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ContactDTO {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String company;

    @NotBlank(message = "Chủ đề không được để trống")
    private String subject;

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String message;

    private boolean newsletter;

    public ContactDTO() {
    }

    public ContactDTO(String name, String phone, String email, String company, String subject, String message, boolean newsletter) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.subject = subject;
        this.message = message;
        this.newsletter = newsletter;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isNewsletter() { return newsletter; }
    public void setNewsletter(boolean newsletter) { this.newsletter = newsletter; }

    @Override
    public String toString() {
        return "ContactDto{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", newsletter=" + newsletter +
                '}';
    }
}