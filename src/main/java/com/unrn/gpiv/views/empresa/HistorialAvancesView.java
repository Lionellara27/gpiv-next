package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.InformeAvance;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.time.LocalDate;

public class HistorialAvancesView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaActual;

    // --- CAMPOS DEL FORMULARIO ---
    private TextField txtTitulo = new TextField("Título del Avance (Ej: Techado Galpón)");
    private DatePicker dateFecha = new DatePicker("Fecha de la Novedad");
    private TextArea txtDescripcion = new TextArea("Descripción / Detalles de la obra");

    // Upload de Vaadin para el PDF
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadPdf = new Upload(buffer);

    private Button btnRegistrar = new Button("Registrar Avance", VaadinIcon.PLUS.create());

    // Variables temporales para el archivo
    private byte[] pdfBytesTemporal;
    private String nombreArchivoTemporal;

    // --- GRILLA PARA VER EL HISTORIAL ---
    private Grid<InformeAvance> gridAvances = new Grid<>(InformeAvance.class, false);

    public HistorialAvancesView(Empresa empresaActual, EmpresaService empresaService) {
        this.empresaActual = empresaActual;
        this.empresaService = empresaService;

        setWidthFull();
        setPadding(false);

        H3 tituloForm = new H3("Declarar Nuevo Avance de Obra");

        // 1. CONFIGURAR COMPONENTES DEL FORMULARIO
        txtTitulo.setWidthFull();
        dateFecha.setWidth("300px");
        dateFecha.setValue(LocalDate.now());
        txtDescripcion.setWidthFull();
        txtDescripcion.setHeight("100px");

        // Configuración estricta para PDF
        uploadPdf.setAcceptedFileTypes("application/pdf");
        uploadPdf.setMaxFiles(1);
        uploadPdf.setDropLabel(new Span("Arrastre su plano/informe en PDF aquí"));

        uploadPdf.addSucceededListener(event -> {
            try {
                pdfBytesTemporal = buffer.getInputStream().readAllBytes();
                nombreArchivoTemporal = event.getFileName();
                Notification.show("PDF procesado: " + nombreArchivoTemporal);
            } catch (Exception ex) {
                Notification.show("Error al leer el PDF", 5000, Notification.Position.MIDDLE);
            }
        });

        btnRegistrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRegistrar.addClickListener(e -> guardarAvance());

        // 2. ARMAR EL DISEÑO DEL FORMULARIO
        HorizontalLayout filaArriba = new HorizontalLayout(txtTitulo, dateFecha);
        filaArriba.setWidthFull();

        HorizontalLayout filaAbajo = new HorizontalLayout(uploadPdf, btnRegistrar);
        filaAbajo.setWidthFull();
        filaAbajo.setAlignItems(Alignment.END);

        VerticalLayout formCarga = new VerticalLayout(filaArriba, txtDescripcion, filaAbajo);
        formCarga.getStyle().set("background-color", "#ffffff").set("border-radius", "10px")
                .set("padding", "1.5em").set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        // 3. CONFIGURAR LA GRILLA DEL HISTORIAL
        H3 tituloGrilla = new H3("Historial Enviado al Parque");
        gridAvances.addColumn(InformeAvance::getFechaEvaluacion).setHeader("Fecha").setSortable(true);
        gridAvances.addColumn(InformeAvance::getTitulo).setHeader("Título");
        gridAvances.addColumn(i -> i.getEstadoCumplimiento() != null ? i.getEstadoCumplimiento().name() : "PENDIENTE").setHeader("Estado");
        gridAvances.addColumn(i -> i.getNombreArchivoPdf() != null ? "Adjunto ✅" : "Sin archivo").setHeader("PDF");

        actualizarGrilla();

        // Agregamos todo a la vista principal
        add(tituloForm, formCarga, tituloGrilla, gridAvances);
    }

    private void guardarAvance() {
        if (txtTitulo.isEmpty() || dateFecha.isEmpty() || txtDescripcion.isEmpty()) {
            Notification.show("Por favor, completá título, fecha y descripción.");
            return;
        }
        if (pdfBytesTemporal == null) {
            Notification.show("⚠️ Es obligatorio adjuntar el PDF.");
            return;
        }

        // DESPERTADOR: Recargamos versión fresca
        this.empresaActual = empresaService.obtenerEmpresaPorRepresentante(empresaActual.getRepresentante());

        InformeAvance nuevoAvance = new InformeAvance();
        nuevoAvance.setTitulo(txtTitulo.getValue());
        nuevoAvance.setFechaEvaluacion(dateFecha.getValue());
        nuevoAvance.setObservaciones(txtDescripcion.getValue());
        nuevoAvance.setArchivoPdf(pdfBytesTemporal);
        nuevoAvance.setNombreArchivoPdf(nombreArchivoTemporal);
        // nuevoAvance.setEstadoCumplimiento(EstadoCumplimiento.PENDIENTE); // Descomentar si tu Enum se llama así
        nuevoAvance.setEmpresa(this.empresaActual);

        // Agregamos y guardamos
        this.empresaActual.getInformesDeAvance().add(nuevoAvance);
        empresaService.actualizarEmpresa(this.empresaActual);

        // Recargamos para actualizar grilla
        this.empresaActual = empresaService.obtenerEmpresaPorRepresentante(empresaActual.getRepresentante());

        // Limpiamos pantalla
        txtTitulo.clear();
        dateFecha.setValue(LocalDate.now());
        txtDescripcion.clear();
        uploadPdf.clearFileList();
        pdfBytesTemporal = null;
        nombreArchivoTemporal = null;

        actualizarGrilla();
        Notification.show("¡Avance registrado y enviado al Administrador!");
    }

    private void actualizarGrilla() {
        gridAvances.setItems(empresaActual.getInformesDeAvance());
    }
}