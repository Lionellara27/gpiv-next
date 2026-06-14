package com.unrn.gpiv.controller;

import com.unrn.gpiv.dto.LoginRequestDTO;
import com.unrn.gpiv.dto.LoginResponseDTO;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.model.Usuario; // O la clase que retorne tu loginGeneral
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // Le dice a Spring que esto devuelve JSON, no vistas
@RequestMapping("/api/auth") // La ruta base para este controlador
@CrossOrigin(origins = "http://localhost:5173") // Permite que React local le hable al back sin errores de CORS
public class AuthController {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            // Reutilizamos lógica sin tocar el Service
            Usuario usuario = empresaService.loginGeneral(request.getEmail(), request.getPassword());

            String tokenSimulado = "jwt-simulado-123456";

            LoginResponseDTO response = new LoginResponseDTO(
                    tokenSimulado,
                    usuario.getId(),
                    usuario.getRol().name(),
                    usuario.getUsername() //
            );

            return ResponseEntity.ok(response); // Devuelve HTTP 200 con el JSON

        } catch (Exception e) {
            // Si loginGeneral tira excepción (ej: credenciales inválidas)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // HTTP 401
                    .body(Map.of("message", "Usuario o contraseña incorrectos"));
        }
    }
}