package com.unrn.gpiv.model;

import com.unrn.gpiv.common.TipoServicio;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoServicio tipoServicio;

    private String descripcion;

    // true = funcionando/disponible, false = en obra o fuera de servicio
    private boolean estadoDisponibilidad;

    // Relación con Lotes: Muchos servicios pueden estar en muchos lotes
    @ManyToMany(mappedBy = "servicios")
    private List<Lote> lotes;

    public Servicio() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipo) { this.tipoServicio = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String desc) { this.descripcion = desc; }

    public boolean isEstadoDisponibilidad() { return estadoDisponibilidad; }
    public void setEstadoDisponibilidad(boolean estado) { this.estadoDisponibilidad = estado; }

    public List<Lote> getLotes() { return lotes; }
    public void setLotes(List<Lote> lotes) { this.lotes = lotes; }
}