package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoSolicitud;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_radicacion")
public class SolicitudRadicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razonSocialPretendida;
    private LocalDateTime fechaCreacion = LocalDateTime.now(); // fecha que entra
    private LocalDateTime fechaResolucion; //hay que esperar fecha donde se "audita"

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id", referencedColumnName = "id")
    private ProyectoProductivo proyecto;

    @ManyToOne
    @JoinColumn(name = "representante_id")
    private RepresentanteEmpresa representante;

    public SolicitudRadicacion() {}

    // Getters y Setters...


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocialPretendida() {
        return razonSocialPretendida;
    }

    public void setRazonSocialPretendida(String razonSocialPretendida) {
        this.razonSocialPretendida = razonSocialPretendida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public ProyectoProductivo getProyecto() {
        return proyecto;
    }

    public void setProyecto(ProyectoProductivo proyecto) {
        this.proyecto = proyecto;
    }

    public RepresentanteEmpresa getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteEmpresa representante) {
        this.representante = representante;
    }
}