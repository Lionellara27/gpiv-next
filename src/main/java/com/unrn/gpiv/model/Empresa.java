package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_empresa", nullable = false)
    private EstadoEmpresa estadoEmpresa = EstadoEmpresa.INTERESADA;

    @Column(nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String cuit;

    private String direccion; // Dirección legal/administrativa

    @Column(columnDefinition = "bytea")
    private byte[] pdfActaRadicacion;
    private String nombreActaRadicacion;

    @Column(columnDefinition = "bytea")
    private byte[] pdfActaDesadjudicacion;
    private String nombreActaDesadjudicacion;

    private boolean titulada = false; // Si ya tiene el título de propiedad del lote

    @Column(name = "tipo_sociedad")
    private String tipoSociedad;

    @Column(name = "telefono_emergencia")
    private String telefonoEmergencia;

    @Column(name = "inscripcion_registral")
    private String inscripcionRegistral;

    private boolean datosFinalesCompletos = false;

    // --- NUEVOS CAMPOS Y RELACIONES PARA LA HU 4 (aVANCE) ---

    @Column(name = "logo", columnDefinition = "bytea")
    private byte[] logo; // Imagen del logo de la empresa

    @Column(columnDefinition = "TEXT")
    private String descripcionPublica; // La descripción para el perfil

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumoMensual> consumosMensuales = new ArrayList<>();

    // --- RELACIONES CORE ---

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", referencedColumnName = "id")
    private ProyectoProductivo proyecto;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private RepresentanteEmpresa representante;

    // cambio la relacion, para que una empresa pueda tener varios lotes
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Lote> lotesAsignados = new ArrayList<>();

    private LocalDate fechaRadicacion; // Fecha efectiva de entrada al Parque

    // --- INFORMES Y SEGUIMIENTO ---

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InformeAvance> informesDeAvance = new ArrayList<>();

    // --- RELACIONES DE INVENTARIO (Módulo Gerente) ---

    // Herramientas que el Parque le prestó a la empresa
    @OneToMany(mappedBy = "prestadoA")
    private List<Recurso> herramientasPrestadas = new ArrayList<>();

    // Herramientas que la empresa trajo al parque (su capital)
    @OneToMany(mappedBy = "propietarioEmpresa")
    private List<Recurso> herramientasAportadas = new ArrayList<>();

    // agrego este metodo para la parte de ver recursos desdes vista admin
    public List<Recurso> getRecursosAsignados() {
        return this.herramientasPrestadas;
    }

    // --- CONSTRUCTORES ---

    public Empresa() {}

    // METODOS PROPIOS
    public void agregarHerramientaPrestada(Recurso recurso) {
        this.herramientasPrestadas.add(recurso);
        recurso.setPrestadoA(this);
    }

    // --- GETTERS Y SETTERS ---

    public EstadoEmpresa getEstadoEmpresa() {
        return estadoEmpresa;
    }

    public void setEstadoEmpresa(EstadoEmpresa estadoEmpresa) {
        this.estadoEmpresa = estadoEmpresa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isTitulada() { return titulada; }
    public void setTitulada(boolean titulada) { this.titulada = titulada; }

    public ProyectoProductivo getProyecto() { return proyecto; }
    public void setProyecto(ProyectoProductivo proyecto) { this.proyecto = proyecto; }

    public RepresentanteEmpresa getRepresentante() { return representante; }
    public void setRepresentante(RepresentanteEmpresa representante) { this.representante = representante; }

    public List<Lote> getLotesAsignados() { return lotesAsignados; }
    public void setLotesAsignados(List<Lote> lotesAsignados) { this.lotesAsignados = lotesAsignados; }

    public LocalDate getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDate fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public List<InformeAvance> getInformesDeAvance() { return informesDeAvance; }
    public void setInformesDeAvance(List<InformeAvance> informes) { this.informesDeAvance = informes; }

    public List<Recurso> getHerramientasPrestadas() { return herramientasPrestadas; }
    public void setHerramientasPrestadas(List<Recurso> herramientasPrestadas) { this.herramientasPrestadas = herramientasPrestadas; }

    public List<Recurso> getHerramientasAportadas() { return herramientasAportadas; }
    public void setHerramientasAportadas(List<Recurso> herramientasAportadas) { this.herramientasAportadas = herramientasAportadas; }

    public String getTipoSociedad() {
        return tipoSociedad;
    }
    public void setTipoSociedad(String tipoSociedad){
        this.tipoSociedad = tipoSociedad;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public String getInscripcionRegistral() {
        return inscripcionRegistral;
    }

    public void setInscripcionRegistral(String inscripcionRegistral) {
        this.inscripcionRegistral = inscripcionRegistral;
    }

    public byte[] getPdfActaRadicacion() {
        return pdfActaRadicacion;
    }

    public void setPdfActaRadicacion(byte[] pdfActaRadicacion) {
        this.pdfActaRadicacion = pdfActaRadicacion;
    }

    public String getNombreActaRadicacion() {
        return nombreActaRadicacion;
    }

    public void setNombreActaRadicacion(String nombreActaRadicacion) {
        this.nombreActaRadicacion = nombreActaRadicacion;
    }

    public byte[] getPdfActaDesadjudicacion() {
        return pdfActaDesadjudicacion;
    }

    public void setPdfActaDesadjudicacion(byte[] pdfActaDesadjudicacion) {
        this.pdfActaDesadjudicacion = pdfActaDesadjudicacion;
    }

    public String getNombreActaDesadjudicacion() {
        return nombreActaDesadjudicacion;
    }

    public void setNombreActaDesadjudicacion(String nombreActaDesadjudicacion) {
        this.nombreActaDesadjudicacion = nombreActaDesadjudicacion;
    }

    public boolean isDatosFinalesCompletos() {
        return datosFinalesCompletos;
    }

    public void setDatosFinalesCompletos(boolean datosFinalesCompletos) {
        this.datosFinalesCompletos = datosFinalesCompletos;
    }

    //parte nueva para los avances
    // --- GETTERS Y SETTERS HU 4 ---

    public byte[] getLogo() { return logo; }
    public void setLogo(byte[] logo) { this.logo = logo; }

    public String getDescripcionPublica() { return descripcionPublica; }
    public void setDescripcionPublica(String descripcionPublica) { this.descripcionPublica = descripcionPublica; }

    public List<Empleado> getEmpleados() { return empleados; }
    public void setEmpleados(List<Empleado> empleados) { this.empleados = empleados; }

    public List<Vehiculo> getVehiculos() { return vehiculos; }
    public void setVehiculos(List<Vehiculo> vehiculos) { this.vehiculos = vehiculos; }

    public List<ConsumoMensual> getConsumosMensuales() { return consumosMensuales; }
    public void setConsumosMensuales(List<ConsumoMensual> consumosMensuales) { this.consumosMensuales = consumosMensuales; }
}
