package com.unrn.gpiv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consumos_mensuales")
public class ConsumoMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mesAnio; // Ej: "05/2026"

    private Double consumoLuz; // kWh
    private Double consumoAgua; // Litros
    private Double consumoGas; // m3

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    public ConsumoMensual() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMesAnio() { return mesAnio; }
    public void setMesAnio(String mesAnio) { this.mesAnio = mesAnio; }

    public Double getConsumoLuz() { return consumoLuz; }
    public void setConsumoLuz(Double consumoLuz) { this.consumoLuz = consumoLuz; }

    public Double getConsumoAgua() { return consumoAgua; }
    public void setConsumoAgua(Double consumoAgua) { this.consumoAgua = consumoAgua; }

    public Double getConsumoGas() { return consumoGas; }
    public void setConsumoGas(Double consumoGas) { this.consumoGas = consumoGas; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}