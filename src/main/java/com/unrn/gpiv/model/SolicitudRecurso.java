package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoSolicitud;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "solicitudes_recursos")
public class SolicitudRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "item_inventario_id")
    private Item itemSolicitado;

    private Integer cantidad;

    @Column(length = 500)
    private String motivo;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    public SolicitudRecurso() {
        this.fechaSolicitud = LocalDate.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Item getItemSolicitado() { return itemSolicitado; }
    public void setItemSolicitado(Item itemSolicitado) { this.itemSolicitado = itemSolicitado; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}