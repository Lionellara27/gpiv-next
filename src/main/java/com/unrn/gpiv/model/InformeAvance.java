package com.unrn.gpiv.model;

import com.unrn.gpiv.common.EstadoCumplimiento;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "informes_avance")
public class InformeAvance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -fecha_evaluacion: DATE
    private LocalDate fechaEvaluacion;

    // -observaciones: String
    @Column(columnDefinition = "TEXT") // Para que no se corte el texto en la DB
    private String observaciones;

    // -estado_cumplimiento (Usando el Enum que ya tenés)
    @Enumerated(EnumType.STRING)
    private EstadoCumplimiento estadoCumplimiento;

    // -responsable_evaluacion: String
    private String responsableEvaluacion;

    // -resultado (booleano: si cumplió todo y se adjudica el lote)
    private boolean resultado;

    // Relación con la Empresa (Para saber de quién es este informe)
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;


    //para guardar los consumos y mostrarlos en el front
    private double energia;
    private double agua;
    private double gas;

    // Constructor vacío (obligatorio)
    public InformeAvance() {}

    // --- CAMPOS NUEVOS PARA LA HU 4 ---
    private String titulo; // Ej: "Finalización de cerramiento perimetral"

    @Lob // Para guardar el PDF en la base de datos
    private byte[] archivoPdf;

    private String nombreArchivoPdf;


    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDate fecha) { this.fechaEvaluacion = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public EstadoCumplimiento getEstadoCumplimiento() { return estadoCumplimiento; }
    public void setEstadoCumplimiento(EstadoCumplimiento estado) { this.estadoCumplimiento = estado; }

    public String getResponsableEvaluacion() { return responsableEvaluacion; }
    public void setResponsableEvaluacion(String responsable) { this.responsableEvaluacion = responsable; }

    public boolean isResultado() { return resultado; }
    public void setResultado(boolean resultado) { this.resultado = resultado; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

//parte nueva para infome pro
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public byte[] getArchivoPdf() { return archivoPdf; }
    public void setArchivoPdf(byte[] archivoPdf) { this.archivoPdf = archivoPdf; }

    public String getNombreArchivoPdf() { return nombreArchivoPdf; }
    public void setNombreArchivoPdf(String nombreArchivoPdf) { this.nombreArchivoPdf = nombreArchivoPdf; }

    //sett y gett para ver los datos en frotn
    public double getEnergia() { return energia; }
    public void setEnergia(double energia) { this.energia = energia; }

    public double getAgua() { return agua; }
    public void setAgua(double agua) { this.agua = agua; }

    public double getGas() { return gas; }
    public void setGas(double gas) { this.gas = gas; }
}