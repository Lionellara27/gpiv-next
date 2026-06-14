package com.unrn.gpiv.model;

import com.unrn.gpiv.common.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proyectos_productivos")
@Getter @Setter
public class ProyectoProductivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreProyecto;

    // --- NUEVO: Lo que agregamos para el formulario ---
    @Column(nullable = false)
    private String superficieRequerida;

    @jakarta.validation.constraints.Min(value = 1, message = "Debe tener al menos 1 empleado")
    @jakarta.validation.constraints.Max(value = 1500, message = "Número irreal para el Parque")
    @Column(nullable = false)
    private Integer cantidadEmpleados;
    // --------------------------------------------------

    private String ingresoBruto;

    @Column(nullable = false)
    private String actividadPrincipal;

    private String actividadSecundaria;
    private String rubro;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private float potenciaSimultanea;
    private String destinoProduccion;

    //agrego la carga de pdf, maximo 3 pdf
    @Lob
    private byte[] adjuntoFase2_1;
    private String nombreAdjunto1;

    @Lob
    private byte[] adjuntoFase2_2;
    private String nombreAdjunto2;

    @Lob
    private byte[] adjuntoFase2_3;
    private String nombreAdjunto3;
    //--------------------------------

    // --- MODIFICADO: De Enum simple a Lista de Enums (para los Checkbox) ---
    @ElementCollection(targetClass = TipoServicio.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "proyecto_servicios", joinColumns = @JoinColumn(name = "proyecto_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "servicio")
    private Set<TipoServicio> serviciosNecesarios = new HashSet<>();
    // ------------------------------------------------------------------------

    @Column(columnDefinition = "TEXT")
    private String impactoAmbiental;

    private String tipoResiduos;

    @Enumerated(EnumType.STRING)
    private Emplazamiento emplazamientoActual;

    private String materiaPrima;

    @Column(name = "pdf_proyecto")
    private byte[] pdfProyecto;

    @Column(name = "nombre_archivo_pdf")
    private String nombreArchivoPdf;

    // Personal Ocupado (Dejamos los otros dos por si Martín los pide a futuro)
    private int cantJerarquico;
    private int cantAdministrativo;

    @Enumerated(EnumType.STRING)
    private TensionAlimentacion tensionAlimentacion;

    @OneToOne(mappedBy = "proyecto")
    private Empresa empresa;

    public ProyectoProductivo() {
    }

    public String getNombre(){
        return nombreProyecto;
    }

    public String getCategoria(){
        return rubro;
    }

    public byte[] getAdjuntoFase2_1() { return adjuntoFase2_1; }
    public void setAdjuntoFase2_1(byte[] adjuntoFase2_1) { this.adjuntoFase2_1 = adjuntoFase2_1; }
    public String getNombreAdjunto1() { return nombreAdjunto1; }
    public void setNombreAdjunto1(String nombreAdjunto1) { this.nombreAdjunto1 = nombreAdjunto1; }

    public byte[] getAdjuntoFase2_2() { return adjuntoFase2_2; }
    public void setAdjuntoFase2_2(byte[] adjuntoFase2_2) { this.adjuntoFase2_2 = adjuntoFase2_2; }
    public String getNombreAdjunto2() { return nombreAdjunto2; }
    public void setNombreAdjunto2(String nombreAdjunto2) { this.nombreAdjunto2 = nombreAdjunto2; }

    public byte[] getAdjuntoFase2_3() { return adjuntoFase2_3; }
    public void setAdjuntoFase2_3(byte[] adjuntoFase2_3) { this.adjuntoFase2_3 = adjuntoFase2_3; }
    public String getNombreAdjunto3() { return nombreAdjunto3; }
    public void setNombreAdjunto3(String nombreAdjunto3) { this.nombreAdjunto3 = nombreAdjunto3; }
}
