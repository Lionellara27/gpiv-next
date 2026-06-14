package com.unrn.gpiv.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historial_radicaciones")
public class HistorialRadicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String razonSocialEmpresa;

    @Column(nullable = false)
    private String cuitEmpresa;

    @Column(nullable = false)
    private String nomenclaturaLote; // Ej: "Manzana 32 - Lote 04"

    @Column(nullable = false)
    private LocalDate fechaAsignacion;

    private LocalDate fechaDesasignacion; // null si la empresa sigue ahí activa

    // --- CONSTRUCTORES ---
    public HistorialRadicacion() {}

    public HistorialRadicacion(String razonSocialEmpresa, String cuitEmpresa, String nomenclaturaLote, LocalDate fechaAsignacion) {
        this.razonSocialEmpresa = razonSocialEmpresa;
        this.cuitEmpresa = cuitEmpresa;
        this.nomenclaturaLote = nomenclaturaLote;
        this.fechaAsignacion = fechaAsignacion;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public String getRazonSocialEmpresa() { return razonSocialEmpresa; }
    public void setRazonSocialEmpresa(String razonSocialEmpresa) { this.razonSocialEmpresa = razonSocialEmpresa; }
    public String getCuitEmpresa() { return cuitEmpresa; }
    public void setCuitEmpresa(String cuitEmpresa) { this.cuitEmpresa = cuitEmpresa; }
    public String getNomenclaturaLote() { return nomenclaturaLote; }
    public void setNomenclaturaLote(String nomenclaturaLote) { this.nomenclaturaLote = nomenclaturaLote; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public LocalDate getFechaDesasignacion() { return fechaDesasignacion; }
    public void setFechaDesasignacion(LocalDate fechaDesasignacion) { this.fechaDesasignacion = fechaDesasignacion; }
}