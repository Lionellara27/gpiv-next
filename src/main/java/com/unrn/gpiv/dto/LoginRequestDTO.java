package com.unrn.gpiv.dto;

public class LoginRequestDTO {
    private String email;
    private String password;

    // Getters y Setters obligatorios
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}