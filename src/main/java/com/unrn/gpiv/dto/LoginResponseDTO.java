package com.unrn.gpiv.dto;

public class LoginResponseDTO {
    private String token;
    private Long id;
    private String rol;
    private String username; // Modificado acá

    public LoginResponseDTO(String token, Long id, String rol, String username) {
        this.token = token;
        this.id = id;
        this.rol = rol;
        this.username = username; // Modificado acá
    }

    // Getters
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getRol() { return rol; }
    public String getUsername() { return username; } // Y su getter
}