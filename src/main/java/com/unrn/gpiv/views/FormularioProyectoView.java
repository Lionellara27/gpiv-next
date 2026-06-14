package com.unrn.gpiv.views;

import com.unrn.gpiv.common.TipoServicio;
import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("formulario-proyecto")
public class FormularioProyectoView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EmpresaService empresaService;

    // Componentes de la vista
    private TextField txtNombreProyecto = new TextField("Razón Social / Nombre del Proyecto");
    private TextArea txtActividad = new TextArea("Descripción de la Actividad Principal");
    private com.vaadin.flow.component.radiobutton.RadioButtonGroup<String> selSuperficie = new com.vaadin.flow.component.radiobutton.RadioButtonGroup<>();
    private IntegerField txtEmpleados = new IntegerField("Cantidad aproximada de empleados");
    private CheckboxGroup<TipoServicio> chkServicios = new CheckboxGroup<>();
    private TextArea txtImpacto = new TextArea("Impacto Ambiental y Residuos");

    // Upload
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadPdf = new Upload(buffer);

    // Binder para ProyectoProductivo
    private Binder<ProyectoProductivo> binder = new Binder<>(ProyectoProductivo.class);

    // 🎯 VARIABLES TEMPORALES PARA ATRAPAR EL PDF EN EL AIRE (NUEVO)
    private byte[] pdfBytesTemporal = null;
    private String pdfNombreTemporal = null;

    private boolean esEdicion = false;
    private SolicitudRadicacion solicitudExistente;
    private H2 titulo = new H2("Proyecto Productivo");
    private Button btnEnviar = new Button("ENVIAR SOLICITUD");

    public FormularioProyectoView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-y", "auto");
        getStyle().set("background-color", "#f5f7fa");

        // 🚀 ACÁ LLAMAMOS A TU MÉTODO PARA QUE LOS CAMPOS NO QUEDEN NULL
        configurarValidaciones();

        // Limitamos a que solo puedan subir PDFs
        uploadPdf.setAcceptedFileTypes("application/pdf", ".pdf");

        // Cuando el archivo termina de cargar en el cuadradito, lo leemos al instante
        uploadPdf.addSucceededListener(event -> {
            try {
                pdfBytesTemporal = buffer.getInputStream().readAllBytes();
                pdfNombreTemporal = event.getFileName();
                Notification.show("PDF procesado y listo para enviar.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error al leer el archivo PDF.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Si el usuario se arrepiente y borra el archivo del cuadradito, limpiamos las variables
        uploadPdf.getElement().addEventListener("file-remove", event -> {
            pdfBytesTemporal = null;
            pdfNombreTemporal = null;
        });

        VerticalLayout formCard = new VerticalLayout();
        formCard.setWidth("700px");
        formCard.getStyle().set("background-color", "white");
        formCard.getStyle().set("padding", "2.5em").set("border-radius", "15px");
        formCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        formCard.setAlignItems(Alignment.STRETCH);

        // Header
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        titulo.getStyle().set("text-align", "center").set("color", "#0063BE").set("margin-top", "0");

        Paragraph subtitulo = new Paragraph("Detallá la actividad para evaluar la mejor asignación de lote.");
        subtitulo.getStyle().set("text-align", "center").set("color", "#666");

        // Configuración de campos
        txtNombreProyecto.setPlaceholder("Ej: Planta de ensamblaje metalúrgico");
        selSuperficie.setLabel("Superficie requerida");
        selSuperficie.setItems("1200 m²", "2500 m²", "5000 m²", "Más de 5000 m²");
        selSuperficie.addThemeVariants(com.vaadin.flow.component.radiobutton.RadioGroupVariant.LUMO_VERTICAL);

        chkServicios.setLabel("Servicios necesarios");
        chkServicios.setItems(TipoServicio.values());
        chkServicios.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        // Botonera
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setJustifyContentMode(JustifyContentMode.END);

        Button btnCancelar = new Button("Cancelar", e -> {
            if (esEdicion) {
                getUI().ifPresent(ui -> ui.navigate("mi-proyecto"));
            } else {
                getUI().ifPresent(ui -> ui.navigate(""));
            }
        });
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.getStyle().set("background-color", "#009A3B");
        btnEnviar.addClickListener(e -> ejecutarAccionPrincipal());

        botonera.add(btnCancelar, btnEnviar);

        formCard.add(logo, lineasBandera, titulo, subtitulo, txtNombreProyecto, txtActividad,
                selSuperficie, txtEmpleados, chkServicios, txtImpacto, new Span("Adjuntar Proyecto (PDF)"), uploadPdf, botonera);

        add(formCard);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        if (parameter != null) {
            this.esEdicion = true;
            this.solicitudExistente = empresaService.obtenerSolicitudPorId(parameter);

            if (solicitudExistente != null) {
                txtNombreProyecto.setValue(solicitudExistente.getRazonSocialPretendida());
                binder.readBean(solicitudExistente.getProyecto());

                titulo.setText("Modificar Mi Proyecto");
                btnEnviar.setText("GUARDAR CAMBIOS");
                btnEnviar.getStyle().set("background-color", "#0063BE");
            }
        }
    }

    private void ejecutarAccionPrincipal() {
        try {
            RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
            if (rep == null) {
                Notification.show("Error: Sesión expirada", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            ProyectoProductivo proyecto = esEdicion ? solicitudExistente.getProyecto() : new ProyectoProductivo();

            // Si pasa las validaciones de tu método configurarValidaciones...
            if (binder.writeBeanIfValid(proyecto)) {

                // 🚀 Usamos las variables temporales que atraparon el PDF real
                if (pdfBytesTemporal != null && pdfNombreTemporal != null) {
                    proyecto.setPdfProyecto(pdfBytesTemporal);
                    proyecto.setNombreArchivoPdf(pdfNombreTemporal);
                } else if (!esEdicion) {
                    Notification.show("Debe adjuntar el PDF del proyecto antes de enviar.", 4000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (esEdicion) {
                    solicitudExistente.setRazonSocialPretendida(txtNombreProyecto.getValue());
                    empresaService.actualizarSolicitud(solicitudExistente);
                    Notification.show("Cambios guardados con éxito.");
                } else {
                    empresaService.recibirSolicitud(proyecto, rep, txtNombreProyecto.getValue());
                    Notification.show("Solicitud enviada correctamente.");
                }

                getUI().ifPresent(ui -> ui.navigate(esEdicion ? "mi-proyecto" : ""));
            } else {
                // Si el usuario dejó un campo vacío, le avisamos
                Notification.show("Por favor, completá todos los campos obligatorios.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Error al guardar: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // 🚀 TU MÉTODO ORIGINAL AL FINAL DE LA CLASE (INTACTO)
    private void configurarValidaciones() {
        binder.forField(txtNombreProyecto).asRequired("El nombre es obligatorio").bind(ProyectoProductivo::getNombreProyecto, ProyectoProductivo::setNombreProyecto);
        binder.forField(txtActividad).asRequired("Obligatorio").bind(ProyectoProductivo::getActividadPrincipal, ProyectoProductivo::setActividadPrincipal);
        binder.forField(selSuperficie).asRequired("Obligatorio").bind(ProyectoProductivo::getSuperficieRequerida, ProyectoProductivo::setSuperficieRequerida);
        binder.forField(txtEmpleados).asRequired("Obligatorio").bind(ProyectoProductivo::getCantidadEmpleados, ProyectoProductivo::setCantidadEmpleados);
        binder.forField(chkServicios).asRequired("Obligatorio").bind(ProyectoProductivo::getServiciosNecesarios, ProyectoProductivo::setServiciosNecesarios);
        binder.forField(txtImpacto).bind(ProyectoProductivo::getImpactoAmbiental, ProyectoProductivo::setImpactoAmbiental);
    }
}


   /*v iejo private void ejecutarAccionPrincipal() {

        try {
            RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
            if (rep == null) {
                Notification.show("Error: Sesión expirada", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            ProyectoProductivo proyecto = esEdicion ? solicitudExistente.getProyecto() : new ProyectoProductivo();

            if (binder.writeBeanIfValid(proyecto)) {
                // PDF obligatorio solo en creación
                if (buffer.getFileName().isEmpty() && !esEdicion) {
                    Notification.show("Debe subir el PDF del proyecto", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (!buffer.getFileName().isEmpty()) {
                    proyecto.setPdfProyecto(buffer.getInputStream().readAllBytes());
                    proyecto.setNombreArchivoPdf(buffer.getFileName());
                }

                if (esEdicion) {
                    solicitudExistente.setRazonSocialPretendida(txtNombreProyecto.getValue());
                    empresaService.actualizarSolicitud(solicitudExistente);
                    Notification.show("Solicitud actualizada con éxito");
                } else {
                    empresaService.recibirSolicitud(proyecto, rep, txtNombreProyecto.getValue());
                    Notification.show("Solicitud enviada correctamente");
                }

                getUI().ifPresent(ui -> ui.navigate(esEdicion ? "mi-proyecto" : ""));
            }
        } catch (Exception ex) {
            Notification.show("Error crítico: " + ex.getMessage(), 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }*/
/*
    private void configurarUpload() {
        uploadPdf.setAcceptedFileTypes("application/pdf");
        uploadPdf.setMaxFiles(1);
    }

    private void configurarValidaciones() {
        binder.forField(txtActividad).asRequired("Obligatorio").bind(ProyectoProductivo::getActividadPrincipal, ProyectoProductivo::setActividadPrincipal);
        binder.forField(selSuperficie).asRequired("Obligatorio").bind(ProyectoProductivo::getSuperficieRequerida, ProyectoProductivo::setSuperficieRequerida);
        binder.forField(txtEmpleados).asRequired("Obligatorio").bind(ProyectoProductivo::getCantidadEmpleados, ProyectoProductivo::setCantidadEmpleados);
        binder.forField(chkServicios).asRequired("Obligatorio").bind(ProyectoProductivo::getServiciosNecesarios, ProyectoProductivo::setServiciosNecesarios);
        binder.forField(txtImpacto).bind(ProyectoProductivo::getImpactoAmbiental, ProyectoProductivo::setImpactoAmbiental);
    }
}

/*package com.unrn.gpiv.views;

import com.unrn.gpiv.model.ProyectoProductivo;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Route("formulario-proyecto")
public class FormularioProyectoView extends VerticalLayout {

    // Componentes que se vinculan al modelo
    private TextField txtNombreProyecto = new TextField("Razón Social / Nombre del Proyecto");
    private TextArea txtActividad = new TextArea("Descripción de la Actividad Principal");
    //private NumberField txtSuperficie = new NumberField("Superficie requerida (m²)");
    private NumberField txtEmpleados = new NumberField("Empleados estimados");
    private TextArea txtImpacto = new TextArea("Impacto Ambiental y Residuos");
    //radio button
    private com.vaadin.flow.component.radiobutton.RadioButtonGroup<String> selSuperficie = new com.vaadin.flow.component.radiobutton.RadioButtonGroup<>();

    // Componentes para el PDF
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload uploadPdf = new Upload(buffer);

    private Binder<ProyectoProductivo> binder = new Binder<>(ProyectoProductivo.class);

    public FormularioProyectoView(@Autowired EmpresaService empresaService) {
        // --- CONFIGURACIÓN DE LA VISTA (Para que no se corte arriba) ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-y", "auto"); // Permite scroll
        getStyle().set("background-color", "#f5f7fa");

        configurarValidaciones();
        configurarUpload();

        VerticalLayout formCard = new VerticalLayout();

        formCard.setWidth("700px");
        formCard.getStyle().set("background-color", "white");
        formCard.getStyle().set("padding", "2.5em");
        formCard.getStyle().set("border-radius", "15px");
        formCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");

        formCard.getStyle().set("margin-top", "2em");
        formCard.getStyle().set("margin-bottom", "2em");

        formCard.setAlignItems(Alignment.STRETCH);

        // LOGO Y BANDERA (Tu diseño original)
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        H2 titulo = new H2("Proyecto Productivo");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");
        titulo.getStyle().set("margin-top", "0");

        Paragraph subtitulo = new Paragraph("Paso 2: Detallá la actividad que vas a realizar para que la Administración evalúe qué lote se adapta mejor a tus necesidades.");
        subtitulo.getStyle().set("text-align", "center");
        subtitulo.getStyle().set("color", "#666");

        // Campos de texto con tus placeholders originales
        txtNombreProyecto.setPlaceholder("Ej: Planta de ensamblaje metalúrgico");
        txtActividad.setPlaceholder("Describí en detalle qué se va a producir o qué servicio se va a brindar...");
        txtActividad.setMinHeight("100px");

        HorizontalLayout filaMedidas = new HorizontalLayout();
        filaMedidas.setWidthFull();
        //txtSuperficie.setPlaceholder("Ej: 2500");
        //txtSuperficie.setWidth("50%");
        txtEmpleados.setPlaceholder("Ej: 15");
        txtEmpleados.setWidth("50%");
        filaMedidas.add(txtEmpleados);

        selSuperficie.setLabel("Superficie requerida para desarrollar la actividad");
        selSuperficie.setItems("1200 m²", "2500 m²", "5000 m²", "Más de 5000 m²");
        selSuperficie.addThemeVariants(com.vaadin.flow.component.radiobutton.RadioGroupVariant.LUMO_VERTICAL);
        selSuperficie.getStyle().set("font-weight", "bold").set("margin-top", "1em");

        CheckboxGroup<String> chkServicios = new CheckboxGroup<>();
        chkServicios.setLabel("Servicios de Infraestructura necesarios");
        chkServicios.setItems("Agua Potable", "Energía Eléctrica (Trifásica)", "Gas Industrial", "Internet / Fibra Óptica");
        chkServicios.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        txtImpacto.setPlaceholder("¿Genera efluentes líquidos, gases o ruidos molestos? Detallar.");
        txtImpacto.setMinHeight("80px");

        //Carga de PDF--------------------
        Span etiquetaPdf = new Span("Adjuntar Análisis de Mercado y Objetivos (PDF)");
        etiquetaPdf.getStyle().set("font-weight", "bold").set("margin-top", "1em").set("color", "#444");

        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setJustifyContentMode(JustifyContentMode.END);

        Button btnCancelar = new Button("Cancelar", e -> getUI().ifPresent(ui -> ui.navigate("")));
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnEnviar = new Button("ENVIAR SOLICITUD", e -> {
            try {
                ProyectoProductivo proyecto = new ProyectoProductivo();
                RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

                // DEBUG 1: ¿Existe el usuario?
                if (rep == null) {
                    Notification.show("ERROR: No hay usuario en sesión. Volvé a registrarte.", 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // DEBUG 2: ¿Los datos del formulario son válidos?
                if (binder.writeBeanIfValid(proyecto)) {

                    if (buffer.getFileName().isEmpty()) {
                        Notification.show("Debe subir el PDF del proyecto", 3000, Notification.Position.MIDDLE);
                        return;
                    }

                    // Procesamos el archivo
                    proyecto.setPdfProyecto(buffer.getInputStream().readAllBytes());
                    proyecto.setNombreArchivoPdf(buffer.getFileName());

                    // DEBUG 3: Llamada al servicio
                    System.out.println("Enviando solicitud al servicio...");
                    empresaService.recibirSolicitud(proyecto, rep, txtNombreProyecto.getValue());
                    System.out.println("¡Servicio completado con éxito!");

                    Notification.show("Solicitud enviada correctamente", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate(""));

                } else {
                    // Si entra acá, hay un campo mal cargado que el Binder detectó
                    Notification.show("Error de validación: Revisá los campos rojos", 3000, Notification.Position.MIDDLE);
                    binder.validate(); // Esto fuerza a que se pongan rojos los campos con error
                }
            } catch (Exception ex) {
                // ESTO ES CLAVE: Si hay un error de base de datos o de código, saltará acá
                ex.printStackTrace(); // Esto imprime el error detallado en la consola de IntelliJ
                Notification.show("ERROR CRÍTICO: " + ex.getMessage(), 10000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.getStyle().set("background-color", "#009A3B");

        botonera.add(btnCancelar, btnEnviar);


        formCard.add(logo, lineasBandera, titulo, subtitulo, txtNombreProyecto, txtActividad,
                selSuperficie, chkServicios, txtImpacto, etiquetaPdf, uploadPdf, botonera);

        add(formCard);
    }

    private void configurarUpload() {
        uploadPdf.setAcceptedFileTypes("application/pdf");
        uploadPdf.setMaxFiles(1);
        uploadPdf.setUploadButton(new Button("Seleccionar PDF"));
    }

    private void configurarValidaciones() {
        binder.forField(txtActividad)
                .asRequired("La descripción de la actividad es obligatoria")
                .bind(ProyectoProductivo::getActividadPrincipal, ProyectoProductivo::setActividadPrincipal);

        binder.forField(txtEmpleados)
                .bind(p -> (double) p.getCantProduccion(),
                        (p, v) -> p.setCantProduccion(v.intValue()));

        binder.bindInstanceFields(this);
    }
}*/