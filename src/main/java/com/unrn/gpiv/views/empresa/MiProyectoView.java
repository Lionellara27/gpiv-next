package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Mi Proyecto | SGPIV")
@Route(value = "mi-proyecto", layout = MainLayout.class)
public class MiProyectoView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiProyectoView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);

        // 1. RECUPERAR USUARIO DE SESIÓN
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado. Esta vista es para representantes de empresas."));
            return;
        }

        // 2. BUSCAR SOLICITUD REAL EN LA BD
        SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(logueado);

        if (solicitud == null) {
            renderizarVistaSinProyecto();
            return;
        }

        // 3. EXTRAER DATOS
        ProyectoProductivo proyecto = solicitud.getProyecto();
        EstadoSolicitud estado = solicitud.getEstado();

        // --- UI PRINCIPAL ---
        H2 titulo = new H2("Mi Proyecto Productivo");

        // --- TARJETA DE ESTADO ---
        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidthFull();
        statusCard.getStyle().set("background-color", "#f8f9fa").set("border-radius", "15px");
        statusCard.setPadding(true);

        H3 sub = new H3("Estado de la Solicitud");
        Span badgeEstado = new Span(estado.name());
        configurarEstiloBadge(badgeEstado, estado);

        Paragraph infoEstado = new Paragraph(obtenerMensajeEstado(estado));
        infoEstado.getStyle().set("color", "#666");

        // --- BOTONES DE ACCIÓN ---
        HorizontalLayout acciones = new HorizontalLayout();

        // BOTÓN MODIFICAR (Solo en fase 1 PENDIENTE)
        Button btnModificar = new Button("Modificar Solicitud", VaadinIcon.EDIT.create());
        btnModificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        if (estado != EstadoSolicitud.PENDIENTE) {
            btnModificar.setEnabled(false);
            btnModificar.setTooltipText("No se puede editar: el proyecto ya está en evaluación o pre-aprobado.");
        }

        btnModificar.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("formulario-proyecto/" + solicitud.getId()));
        });

        acciones.add(btnModificar);

        // Configurar descarga del PDF Inicial de la Fase 1
        if (proyecto != null) {
            configurarBotonPDF(acciones, proyecto, logueado);
        }

        // Botón para saltar a Mi Empresa al ser aprobado definitivo
        if (estado == EstadoSolicitud.APROBADA) {
            Button btnIrAEmpresa = new Button("Completar Registro Empresa", VaadinIcon.BUILDING.create());
            btnIrAEmpresa.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            btnIrAEmpresa.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("mi-empresa")));
            acciones.add(btnIrAEmpresa);
        }

        statusCard.add(sub, badgeEstado, infoEstado, acciones);

        add(titulo, statusCard);

        //FORMULARIO DE CARGA FASE 2 (Si está PRE_APROBADO)
        if (estado == EstadoSolicitud.PRE_APROBADO) {
            VerticalLayout panelFase2 = new VerticalLayout();
            panelFase2.getStyle().set("background-color", "#EFF6FF")
                    .set("border", "1px solid #BFDBFE")
                    .set("border-radius", "15px").set("padding", "20px");

            panelFase2.add(new H3("📁 Carga de Documentación Ejecutiva (Fase 2)"));
            panelFase2.add(new Paragraph("Arrastrá o seleccioná hasta 3 archivos en formato PDF con la documentación solicitada (Planos de obra, Análisis Financiero, Análisis de Mercado, etc.)."));

            MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
            Upload upload = new Upload(buffer);
            upload.setAcceptedFileTypes("application/pdf");
            upload.setMaxFiles(3); // El límite acordado

            Button btnEnviarFase2 = new Button("Enviar Documentación Ejecutiva", VaadinIcon.PAPERPLANE.create());
            btnEnviarFase2.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            btnEnviarFase2.setEnabled(false); // Deshabilitado hasta que suba algo

            // Monitoreamos la cantidad de archivos agregados para prender el botón
            upload.addFileRejectedListener(e -> Notification.show(e.getErrorMessage(), 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR));
            upload.addFinishedListener(e -> btnEnviarFase2.setEnabled(!buffer.getFiles().isEmpty()));

            btnEnviarFase2.addClickListener(e -> {
                List<String> nombres = new ArrayList<>(buffer.getFiles());

                try {
                    byte[] doc1 = nombres.size() > 0 ? obtenerBytes(buffer.getInputStream(nombres.get(0))) : null;
                    String nom1 = nombres.size() > 0 ? nombres.get(0) : null;

                    byte[] doc2 = nombres.size() > 1 ? obtenerBytes(buffer.getInputStream(nombres.get(1))) : null;
                    String nom2 = nombres.size() > 1 ? nombres.get(1) : null;

                    byte[] doc3 = nombres.size() > 2 ? obtenerBytes(buffer.getInputStream(nombres.get(2))) : null;
                    String nom3 = nombres.size() > 2 ? nombres.get(2) : null;

                    //uso el metodo para cargar la docu
                    empresaService.enviarDocumentacionFase2(solicitud.getId(), doc1, nom1, doc2, nom2, doc3, nom3);

                    Notification.show("Documentación técnica enviada con éxito.", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // Refrescamos la pantalla para cambiar el estado visual
                    getUI().ifPresent(ui -> ui.getPage().reload());

                } catch (IOException ex) {
                    Notification.show("Error al procesar los archivos: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });

            panelFase2.add(upload, btnEnviarFase2);
            add(panelFase2);
        }

        // --- APARTADO: VISUALIZAR DOCUMENTOS SUBIDOS DE FASE 2 ---
        if ((estado == EstadoSolicitud.DOCUMENTACION_ENVIADA || estado == EstadoSolicitud.APROBADA) && proyecto != null) {
            VerticalLayout panelDescargasFase2 = new VerticalLayout();
            panelDescargasFase2.add(new H3("📋 Documentación Técnica Enviada"));

            HorizontalLayout descargasLayout = new HorizontalLayout();
            descargasLayout.setSpacing(true);

            agregarEnlaceDescargaGenerico(descargasLayout, proyecto.getAdjuntoFase2_1(), proyecto.getNombreAdjunto1());
            agregarEnlaceDescargaGenerico(descargasLayout, proyecto.getAdjuntoFase2_2(), proyecto.getNombreAdjunto2());
            agregarEnlaceDescargaGenerico(descargasLayout, proyecto.getAdjuntoFase2_3(), proyecto.getNombreAdjunto3());

            panelDescargasFase2.add(descargasLayout);
            add(panelDescargasFase2);
        }

        // --- RESUMEN DE DATOS ---
        VerticalLayout datosProyecto = new VerticalLayout();
        datosProyecto.add(new H3("Resumen del Proyecto"));
        datosProyecto.add(new Paragraph("Nombre del proyecto: " + solicitud.getRazonSocialPretendida()));

        if (proyecto != null) {
            datosProyecto.add(new Paragraph("Superficie Requerida: " + proyecto.getSuperficieRequerida() + " m²"));
            String servicios = proyecto.getServiciosNecesarios().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            datosProyecto.add(new Paragraph("Servicios: " + (servicios.isEmpty() ? "Ninguno" : servicios)));
        } else {
            datosProyecto.add(new Paragraph("Atención: Los detalles técnicos del proyecto aún no se han cargado correctamente."));
        }

        add(datosProyecto);
    }

    private byte[] obtenerBytes(InputStream is) throws IOException {
        return is.readAllBytes();
    }

    private void agregarEnlaceDescargaGenerico(HorizontalLayout layout, byte[] archivo, String nombreArchivo) {
        if (archivo != null && nombreArchivo != null) {
            StreamResource res = new StreamResource(nombreArchivo, () -> new ByteArrayInputStream(archivo));
            Anchor link = new Anchor(res, "");
            link.getElement().setAttribute("download", true);

            Button btn = new Button(nombreArchivo, VaadinIcon.DOWNLOAD.create());
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            link.add(btn);
            layout.add(link);
        }
    }

    private void renderizarVistaSinProyecto() {
        add(new H2("Aún no has presentado ningún proyecto."));
        Button btnIrForm = new Button("Cargar Solicitud", e -> getUI().ifPresent(ui -> ui.navigate("formulario-proyecto")));
        btnIrForm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(btnIrForm);
    }

    private void configurarBotonPDF(HorizontalLayout layout, ProyectoProductivo proyecto, RepresentanteEmpresa rep) {
        if (proyecto.getPdfProyecto() != null) {
            String nombreLimpio = rep.getNombreCompleto().replace(" ", "_");
            StreamResource resource = new StreamResource("Solicitud_" + nombreLimpio + ".pdf",
                    () -> new ByteArrayInputStream(proyecto.getPdfProyecto()));

            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);

            Button btnVerPDF = new Button("Ver PDF Solicitud (Fase 1)", VaadinIcon.FILE.create());
            btnVerPDF.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            downloadLink.add(btnVerPDF);
            layout.add(downloadLink);
        }
    }

    private void configurarEstiloBadge(Span badge, EstadoSolicitud estado) {
        badge.getStyle().set("padding", "0.5em 1em").set("border-radius", "20px")
                .set("font-weight", "bold").set("color", "white");

        switch (estado) {
            case PENDIENTE -> badge.getStyle().set("background-color", "#6c757d");
            case EN_EVALUACION -> badge.getStyle().set("background-color", "#0063BE");
            case PRE_APROBADO -> badge.getStyle().set("background-color", "#E28743"); // Naranja intermedio
            case DOCUMENTACION_ENVIADA -> badge.getStyle().set("background-color", "#7030A0"); // Violeta para proceso ejecutivo
            case APROBADA -> badge.getStyle().set("background-color", "#009A3B");
            case RECHAZADA -> badge.getStyle().set("background-color", "#d9534f");
        }
    }

    private String obtenerMensajeEstado(EstadoSolicitud estado) {
        return switch (estado) {
            case PENDIENTE -> "Tu solicitud fue recibida. Podés editarla hasta que comience la evaluación.";
            case EN_EVALUACION -> "El Directorio está analizando tu idea de proyecto. Ya no es posible modificarlo.";
            case PRE_APROBADO -> "¡Tu idea de proyecto pasó la Fase 1 con éxito! Por favor, completá los requisitos de la Fase 2 subiendo la documentación técnica requerida a continuación.";
            case DOCUMENTACION_ENVIADA -> "Tu documentación técnica ejecutiva ha sido enviada con éxito. Los ingenieros y asesores del parque industrial la están revisando formalmente.";
            case APROBADA -> "¡Felicidades! Tu proyecto fue aprobado de forma definitiva. Ahora completá los datos finales de tu empresa."; //para entrar en la cola de asignación de lote. Te deja asignar un lote a una empresa sin haber llenado esos datos que faltan (despues hay que modificarlo con tiempo)
            case RECHAZADA -> "Tu solicitud ha sido rechazada. Por favor, revisá tu correo para ver las observaciones.";
        };
    }
}