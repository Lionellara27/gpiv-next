package com.unrn.gpiv.controller;

// El DTO nuevo que armamos
import com.unrn.gpiv.dto.MenuResponseDTO;

// Los modelos que usaste adentro del método getMenuData
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.SolicitudRadicacion;

// El Enum para el estado (verificá si lo tenés en common o en model)
import com.unrn.gpiv.common.EstadoSolicitud;

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

    @GetMapping("/menu/{usuarioId}")
    // TODO: A futuro, sacaremos el ID directamente del Token JWT en vez de la URL
    public ResponseEntity<MenuResponseDTO> getMenuData(@PathVariable Long usuarioId) {

        // 1. Buscás el usuario en la BD (asumo que tenés un metodo similar)
        Usuario usuario = empresaService.buscarUsuarioPorId(usuarioId);

        if (usuario.getRol().name().equals("ADMIN")) {
            return ResponseEntity.ok(new MenuResponseDTO("ADMIN", false, false));
        }

        // 2. Lógica para la EMPRESA
        boolean solicitudAprobada = false;
        boolean tieneLotes = false;

        try {
            // Reutilizás tus métodos de Vaadin
            SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(usuario);
            if (solicitud != null && solicitud.getEstado() == EstadoSolicitud.APROBADA) {
                solicitudAprobada = true;
            }

            Empresa empresa = empresaService.obtenerEmpresaPorRepresentante(usuario);
            if (empresa != null && !empresa.getLotesAsignados().isEmpty()) {
                tieneLotes = true;
            }
        } catch (Exception e) {
            // Manejo por si no tiene solicitud o empresa aún
        }

        return ResponseEntity.ok(new MenuResponseDTO("EMPRESA", solicitudAprobada, tieneLotes));
    }
}