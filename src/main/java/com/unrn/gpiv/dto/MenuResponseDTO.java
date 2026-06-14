package com.unrn.gpiv.dto;

public class MenuResponseDTO {
    private String rol;
    private boolean solicitudAprobada;
    private boolean tieneLotes;

    public MenuResponseDTO(String rol, boolean solicitudAprobada, boolean tieneLotes) {
        this.rol = rol;
        this.solicitudAprobada = solicitudAprobada;
        this.tieneLotes = tieneLotes;
    }

    // Getters
    public String getRol() { return rol; }
    public boolean isSolicitudAprobada() { return solicitudAprobada; }
    public boolean isTieneLotes() { return tieneLotes; }
}