package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoLote;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La manzana es obligatoria")
    @Column(nullable = false)
    private String manzana;

    @NotBlank(message = "El numero de lote es obligatorio")
    @Column(name = "nro_lote", nullable = false) // Usamos snake_case para la DB
    private String nroLote;

    @Size(max = 255, message = "La ubicacion no puede superar los 255 caracteres")
    private String ubicacion;

    @NotNull(message = "La superficie no puede estar vacia")
    @Positive(message = "La superficie debe ser un numero mayor a cero")
    @Column(nullable = false)
    private Double superficie;

    @NotNull(message = "El estado del lote es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoLote estado = EstadoLote.LIBRE;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "lote_servicio",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private Set<Servicio> servicios = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @PastOrPresent(message = "La fecha de asignacion no puede ser futura")
    @Column(name = "fechaAsignacion")  // fecha de asignacion del lote a la empresa
    private LocalDate fechaAsignacion;

    //Logica
    public boolean isDisponible() {
        return EstadoLote.LIBRE.equals(this.estado) && this.empresa == null;
    }


    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getManzana() { return manzana; }
    public void setManzana(String manzana) { this.manzana = manzana; }

    public String getNroLote() { return nroLote; }
    public void setNroLote(String nroLote) { this.nroLote = nroLote; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) { this.superficie = superficie; }

    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public Set<Servicio> getServicios() { return servicios; }
    public void setServicios(Set<Servicio> servicios) { this.servicios = servicios; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
