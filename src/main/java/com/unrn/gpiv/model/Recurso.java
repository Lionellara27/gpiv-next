package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoConservacionRecurso; // 🟢 IMPORTANTE: Tus nuevos enums
import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "recursos_fisicos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "item_id")
    private Item item;

    private String propietario;
    private String numeroSerie;
    private String ubicacionFisica;

    @Enumerated(EnumType.STRING)
    private EstadoConservacionRecurso estadoConservacion;

    @Enumerated(EnumType.STRING)
    private EstadoMovimientoRecurso estadoMovimiento = EstadoMovimientoRecurso.DISPONIBLE;

    private LocalDate fechaRegistro = LocalDate.now();

    // CASO A: A qué empresa le prestamos el recurso hoy
    @ManyToOne
    @JoinColumn(name = "empresa_prestamo_id")
    private Empresa prestadoA;

    // CASO B: Si el dueño del recurso es una Empresa y no el Parque
    @ManyToOne
    @JoinColumn(name = "empresa_propietaria_id")
    private Empresa propietarioEmpresa;

    public Recurso() {
    }

    // --- GETTERS Y SETTERS COMPLETOS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    // 🟢 Ajustado al tipo Enum
    public EstadoConservacionRecurso getEstadoConservacion() {
        return estadoConservacion;
    }

    // 🟢 Ajustado al tipo Enum
    public void setEstadoConservacion(EstadoConservacionRecurso estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getUbicacionFisica() {
        return ubicacionFisica;
    }

    public void setUbicacionFisica(String ubicacionFisica) {
        this.ubicacionFisica = ubicacionFisica;
    }

    // 🟢 Ajustado al tipo Enum
    public EstadoMovimientoRecurso getEstadoMovimiento() {
        return estadoMovimiento;
    }

    // 🟢 Ajustado al tipo Enum
    public void setEstadoMovimiento(EstadoMovimientoRecurso estadoMovimiento) {
        this.estadoMovimiento = estadoMovimiento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Empresa getPrestadoA() {
        return prestadoA;
    }

    public void setPrestadoA(Empresa prestadoA) {
        this.prestadoA = prestadoA;
    }

    public Empresa getPropietarioEmpresa() {
        return propietarioEmpresa;
    }

    public void setPropietarioEmpresa(Empresa propietarioEmpresa) {
        this.propietarioEmpresa = propietarioEmpresa;
    }
}