package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.repository.SolicitudRadicacionRepository;
import com.unrn.gpiv.repository.LoteRepository;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@PageTitle("Evaluar Solicitudes | SGPIV")
@Route(value = "admin/evaluar", layout = MainLayout.class)
public class EvaluarSolicitudesView extends VerticalLayout {

    private final SolicitudRadicacionRepository solicitudRepo;
    private final EmpresaService empresaService;
    private final LoteRepository loteRepository;
    private final Grid<SolicitudRadicacion> grid = new Grid<>(SolicitudRadicacion.class, false);
    private String pestañaActual = "FASE1"; // FASE1, FASE2, APROBADA, RECHAZADA

    public EvaluarSolicitudesView(@Autowired SolicitudRadicacionRepository solicitudRepo,
								  @Autowired EmpresaService empresaService, LoteRepository loteRepository) {
        this.solicitudRepo = solicitudRepo;
        this.empresaService = empresaService;
		this.loteRepository = loteRepository;

		setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        H2 titulo = new H2("Evaluación de Proyectos Productivos");
        titulo.getStyle().set("margin-bottom", "0");
        Paragraph subtitulo = new Paragraph("Bandeja de entrada del Administrador. Filtre por fases para evaluar.");
        subtitulo.getStyle().set("color", "#666").set("margin-top", "0");

        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "2em");

        // 🎯 AJUSTE DE TABS: Agregamos Fase 1 y Fase 2 explícitamente
        Tab tabFase1 = new Tab("Fase 1: Ideas de Proyecto");
        Tab tabFase2 = new Tab("Fase 2: Documentación Ejecutiva");
        Tab tabAprobadas = new Tab("Aprobadas Finales");
        Tab tabRechazadas = new Tab("Rechazadas");
        Tabs tabs = new Tabs(tabFase1, tabFase2, tabAprobadas, tabRechazadas);
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == tabFase1) {
                pestañaActual = "FASE1";
            } else if (event.getSelectedTab() == tabFase2) {
                pestañaActual = "FASE2";
            } else if (event.getSelectedTab() == tabAprobadas) {
                pestañaActual = "APROBADA";
            } else {
                pestañaActual = "RECHAZADA";
            }
            filtrarGrilla();
        });

        grid.addColumn(s -> s.getFechaCreacion() != null ?
                        s.getFechaCreacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-")
                .setHeader("Fecha")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(SolicitudRadicacion::getRazonSocialPretendida).setHeader("Razón Social").setSortable(true).setAutoWidth(true);
        grid.addColumn(s -> s.getProyecto() != null ? s.getProyecto().getActividadPrincipal() : "Sin especificar").setHeader("Actividad Principal").setAutoWidth(true);
        grid.addComponentColumn(this::crearBadgeEstado).setHeader("Estado Actual").setAutoWidth(true);
        grid.addComponentColumn(this::crearBotoneraAcciones).setHeader("Acciones de Control").setAutoWidth(true);

        filtrarGrilla();

        card.add(tabs, grid);
        add(titulo, subtitulo, card);
    }

    private Span crearBadgeEstado(SolicitudRadicacion solicitud) {
        Span badge = new Span(solicitud.getEstado().name());
        badge.getStyle().set("padding", "5px 12px").set("border-radius", "20px")
                .set("font-weight", "bold").set("font-size", "0.85em").set("color", "white");

        switch (solicitud.getEstado()) {
            case PENDIENTE -> badge.getStyle().set("background-color", "#6c757d"); // Gris neutro
            case EN_EVALUACION -> badge.getStyle().set("background-color", "#0063BE"); // Azul
            case PRE_APROBADO -> badge.getStyle().set("background-color", "#E28743"); // Naranja
            case DOCUMENTACION_ENVIADA -> badge.getStyle().set("background-color", "#7030A0"); // Violeta Ejecutiva
            case APROBADA -> badge.getStyle().set("background-color", "#009A3B"); // Verde
            case RECHAZADA -> badge.getStyle().set("background-color", "#d9534f"); // Rojo
        }
        return badge;
    }

    private HorizontalLayout crearBotoneraAcciones(SolicitudRadicacion solicitud) {
        HorizontalLayout layout = new HorizontalLayout();

        // --- BOTÓN DE VER DETALLES (El ojo) ---
        Button btnVerDetalles = new Button(VaadinIcon.EYE.create());
        btnVerDetalles.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        btnVerDetalles.addClickListener(e -> {
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
                empresaService.marcarComoEnEvaluacion(solicitud.getId());
                solicitud.setEstado(EstadoSolicitud.EN_EVALUACION);
                grid.getDataProvider().refreshItem(solicitud);
            }
            abrirDetallesSolicitud(solicitud);
        });
        layout.add(btnVerDetalles);

        // --- 🔥 SOLUCIÓN DEFINITIVA DESCARGA PDF FASE 1 ---
        if (solicitud.getProyecto() != null && solicitud.getProyecto().getPdfProyecto() != null) {
            String nombreArchivo = solicitud.getProyecto().getNombreArchivoPdf();
            if (nombreArchivo == null || nombreArchivo.isEmpty()) nombreArchivo = "proyecto.pdf";
            if (!nombreArchivo.toLowerCase().endsWith(".pdf")) nombreArchivo += ".pdf";

            StreamResource resource = new StreamResource(nombreArchivo,
                    () -> new ByteArrayInputStream(solicitud.getProyecto().getPdfProyecto()));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            // Convertimos el Anchor directamente en el botón descargable sin disparadores cruzados
            Anchor linkDescarga = new Anchor(resource, "");
            linkDescarga.getElement().setAttribute("download", true);

            Button btnIconoPdf = new Button(VaadinIcon.DOWNLOAD.create());
            btnIconoPdf.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnIconoPdf.setTooltipText("Descargar Idea Proyecto (Fase 1)");

            linkDescarga.add(btnIconoPdf);
            layout.add(linkDescarga);
        }

        // --- ACCIONES DINÁMICAS SEGÚN LA FASE ---
        EstadoSolicitud est = solicitud.getEstado();

        // 1. Acciones para FASE 1 (Lleva a PRE_APROBADO)
        if (est == EstadoSolicitud.PENDIENTE || est == EstadoSolicitud.EN_EVALUACION) {
            Button btnPreAprobar = new Button(VaadinIcon.ARROW_RIGHT.create(), e -> {
                try {
                    // Llamamos al servicio para pasar a la Fase 2
                    empresaService.preAprobarFase1(solicitud.getId());
                    Notification.show("¡Fase 1 Pre-Aprobada con éxito! Esperando documentación ejecutiva.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    filtrarGrilla();
                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            btnPreAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            btnPreAprobar.setTooltipText("Pre-Aprobar Idea (Pasa a Fase 2)");

            Button btnRechazar = new Button(VaadinIcon.CLOSE.create(), e -> {
                solicitud.setEstado(EstadoSolicitud.RECHAZADA);
                solicitud.setFechaResolucion(java.time.LocalDateTime.now());
                solicitudRepo.save(solicitud);
                Notification.show("Solicitud rechazada de forma definitiva.");
                filtrarGrilla();
            });
            btnRechazar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            layout.add(btnPreAprobar, btnRechazar);
        }

        // 2. Acciones para FASE 2 (Lleva a APROBACIÓN DEFINITIVA)
        if (est == EstadoSolicitud.DOCUMENTACION_ENVIADA) {
            Button btnAprobarFinal = new Button(VaadinIcon.CHECK_CIRCLE.create(), e -> {
                // 🎯 En lugar de ejecutar directo, abrimos la ventana flotante obligatoria
                abrirDialogoSubidaActa(solicitud);
            });
            btnAprobarFinal.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            btnAprobarFinal.setTooltipText("Dar Aprobación Radicación Definitiva (Requiere Acta PDF)");

            Button btnRechazarFase2 = new Button(VaadinIcon.CLOSE.create(), e -> {
                solicitud.setEstado(EstadoSolicitud.RECHAZADA);
                solicitud.setFechaResolucion(java.time.LocalDateTime.now());
                solicitudRepo.save(solicitud);
                Notification.show("Documentación Ejecutiva Rechazada.");
                filtrarGrilla();
            });
            btnRechazarFase2.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            layout.add(btnAprobarFinal, btnRechazarFase2);
        }

        // Verifica si la empresa ya tiene lotes asignados
        var empresaOpt = empresaService.obtenerEmpresaPorRepresentante(solicitud.getRepresentante());


        // NUEVA SECCIÓN: Agregar después de línea 189 (después de acciones DOCUMENTACION_ENVIADA)
        // ✨ NUEVO: Botón para ASIGNAR LOTE y pasar a RADICADA
        if (est == EstadoSolicitud.APROBADA &&
                solicitud.getRepresentante() != null &&
                solicitud.getRazonSocialPretendida() != null &&
                empresaOpt.getEstadoEmpresa() == EstadoEmpresa.INTERESADA) {

            Button btnAsignarLote = new Button(VaadinIcon.PLUS.create(), e -> {
                abrirDialogoAsignacionLote(solicitud);
            });
            btnAsignarLote.setTooltipText("Asignar Lote y Radicar Empresa");
            btnAsignarLote.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            layout.add(btnAsignarLote);
        }

        return layout;
    }

    /**
     * Abre un diálogo para asignar un lote libre a la empresa y completar la radicación
     */
    private void abrirDialogoAsignacionLote(SolicitudRadicacion solicitud) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Asignar Lote y Radicar Empresa");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setWidth("600px");

        VerticalLayout layoutModal = new VerticalLayout();
        layoutModal.setPadding(true);
        layoutModal.setSpacing(true);

        Paragraph instruccion = new Paragraph("Seleccione un lote disponible y cargue el Acta de Radicación para " +
                "completar el proceso de radicación de '" + solicitud.getRazonSocialPretendida() + "'.");
        instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

        // ComboBox para seleccionar lote
        com.vaadin.flow.component.combobox.ComboBox<Lote> selectorLote =
                new com.vaadin.flow.component.combobox.ComboBox<>("Lote Disponible");
        selectorLote.setItemLabelGenerator(lote ->
                "Manzana " + lote.getManzana() + ", Lote " + lote.getNroLote() +
                        " (" + lote.getSuperficie() + " m²)");

        // Cargar solo lotes LIBRES
        List<Lote> lotesLibres = new LoteService(loteRepository).buscarPorEstado(EstadoLote.LIBRE);
        selectorLote.setItems(lotesLibres);
        selectorLote.setRequired(true);
        selectorLote.setPlaceholder("Seleccione un lote");

        // Variables temporales para almacenar datos en memoria
        final byte[][] archivoEnMemoria = {null};
        final String[] nombreArchivo = {null};

        // Upload para PDF del Acta
        com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
        upload.setAcceptedFileTypes("application/pdf");
        upload.setMaxFiles(1);

        com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer =
                new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
        upload.setReceiver(buffer);

        // Botón para confirmar
        Button btnConfirmarAsignacion = new Button("Confirmar Asignación y Radicar", VaadinIcon.DATABASE.create());
        btnConfirmarAsignacion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnConfirmarAsignacion.setEnabled(false); // Bloqueado al inicio

        // Listeners
        upload.addSucceededListener(event -> {
            try {
                archivoEnMemoria[0] = buffer.getInputStream().readAllBytes();
                nombreArchivo[0] = event.getFileName();
                btnConfirmarAsignacion.setEnabled(selectorLote.getValue() != null); // Habilita si hay lote
                Notification.show("Acta procesada correctamente.", 2000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error al leer el archivo: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFileRejectedListener(e -> {
            archivoEnMemoria[0] = null;
            nombreArchivo[0] = null;
            btnConfirmarAsignacion.setEnabled(false);
        });

        // Cambios en la selección del lote
        selectorLote.addValueChangeListener(e -> {
            btnConfirmarAsignacion.setEnabled(e.getValue() != null && archivoEnMemoria[0] != null);
        });

        // Botón confirmar
        btnConfirmarAsignacion.addClickListener(click -> {
            try {
                Lote loteSeleccionado = selectorLote.getValue();
                if (loteSeleccionado == null) {
                    Notification.show("Debe seleccionar un lote", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (archivoEnMemoria[0] == null || archivoEnMemoria[0].length == 0) {
                    Notification.show("Debe cargar el Acta de Radicación", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // Asignar lote a la empresa
                Empresa empresaARadicar = empresaService.aprobarRadicacionFinal(
                        solicitud.getId(),
                        archivoEnMemoria[0],
                        nombreArchivo[0]
                );

                // Asignar lote a empresa
                loteSeleccionado.setEmpresa(empresaARadicar);
                loteSeleccionado.setEstado(EstadoLote.OCUPADO);
                loteSeleccionado.setFechaAsignacion(java.time.LocalDate.now());

                // Guardar cambios
                LoteService loteService = new LoteService(loteRepository);
                loteService.guardar(loteSeleccionado);

                empresaARadicar.getLotesAsignados().add(loteSeleccionado);
                empresaService.actualizarEmpresa(empresaARadicar);

                Notification.show("¡Lote asignado y empresa radicada con éxito!", 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                dialog.close();
                filtrarGrilla(); // Actualizar tabla
            } catch (Exception ex) {
                Notification.show("Error al asignar lote: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(btnCancelar, btnConfirmarAsignacion);
        layoutModal.add(instruccion, selectorLote, new H4("Cargar Acta de Radicación"), upload);
        dialog.add(layoutModal);
        dialog.open();
    }

    private void abrirDetallesSolicitud(SolicitudRadicacion solicitud) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Detalles Técnicos: " + solicitud.getRazonSocialPretendida());
        dialog.setWidth("550px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        if (solicitud.getProyecto() != null) {
            var p = solicitud.getProyecto();
            String labelStyle = "font-weight: bold; color: #666; margin-top: 10px;";

            content.add(
                    crearEtiquetaLina("Superficie Requerida:", labelStyle),
                    new Span(p.getSuperficieRequerida() != null ? p.getSuperficieRequerida() : "No especificada"),

                    crearEtiquetaLina("Cantidad de Empleados:", labelStyle),
                    new Span(String.valueOf(p.getCantidadEmpleados())),

                    crearEtiquetaLina("Servicios Necesarios:", labelStyle),
                    crearListaServicios(p.getServiciosNecesarios()),

                    crearEtiquetaLina("Impacto Ambiental y Residuos:", labelStyle),
                    new Paragraph(p.getImpactoAmbiental() != null ? p.getImpactoAmbiental() : "Sin descripción de impacto.")
            );

            // 🎯 NUEVO: Descarga de Documentos Técnicos Fase 2 dentro de la ventana modal
            if (solicitud.getEstado() == EstadoSolicitud.DOCUMENTACION_ENVIADA || solicitud.getEstado() == EstadoSolicitud.APROBADA) {
                VerticalLayout secAdjuntos = new VerticalLayout();
                secAdjuntos.setPadding(false);
                secAdjuntos.setSpacing(true);
                secAdjuntos.add(crearEtiquetaLina("📁 Documentos Adjuntos de Fase 2:", "font-weight: bold; color: #7030A0; margin-top: 15px;"));

                agregarEnlaceDescargaAdmin(secAdjuntos, p.getAdjuntoFase2_1(), p.getNombreAdjunto1());
                agregarEnlaceDescargaAdmin(secAdjuntos, p.getAdjuntoFase2_2(), p.getNombreAdjunto2());
                agregarEnlaceDescargaAdmin(secAdjuntos, p.getAdjuntoFase2_3(), p.getNombreAdjunto3());

                content.add(secAdjuntos);
            }
        }

        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        btnCerrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(btnCerrar);

        dialog.add(content);
        dialog.open();
    }

    private void agregarEnlaceDescargaAdmin(VerticalLayout layout, byte[] archivo, String nombreArchivo) {
        if (archivo != null && nombreArchivo != null) {
            StreamResource res = new StreamResource(nombreArchivo, () -> new ByteArrayInputStream(archivo));
            res.setContentType("application/pdf");

            Anchor link = new Anchor(res, "");
            link.getElement().setAttribute("download", true);

            Button btn = new Button(nombreArchivo, VaadinIcon.DOWNLOAD.create());
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            link.add(btn);
            layout.add(link);
        }
    }

    private Span crearEtiquetaLina(String texto, String estilo) {
        Span label = new Span(texto);
        label.getElement().setAttribute("style", estilo);
        return label;
    }

    private HorizontalLayout crearListaServicios(java.util.Set<com.unrn.gpiv.common.TipoServicio> servicios) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.getStyle().set("margin-top", "5px").set("margin-bottom", "10px");

        if (servicios == null || servicios.isEmpty()) {
            layout.add(new Span("Ninguno"));
        } else {
            servicios.forEach(s -> {
                Span badge = new Span(s.name());
                badge.getElement().getThemeList().add("badge pill small");
                badge.getStyle()
                        .set("background-color", "#eef4ff")
                        .set("color", "#0063BE")
                        .set("font-weight", "600");
                layout.add(badge);
            });
        }
        return layout;
    }

    //Este método se encarga de fabricar el cartel flotante, procesar el archivo en memoria de forma segura
    // y llamar al servicio solo cuando todo este en regla
    private void abrirDialogoSubidaActa(SolicitudRadicacion solicitud) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Subir Acta Oficial de Radicación");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        VerticalLayout layoutModal = new VerticalLayout();
        layoutModal.setPadding(true);
        layoutModal.setSpacing(true);

        Paragraph instruccion = new Paragraph("Para finalizar la radicación de '" + solicitud.getRazonSocialPretendida() +
                "', cargue el acta oficial aprobada en formato PDF. Este documento es obligatorio.");
        instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

        //Variables temporales para alojar el PDF cargado en memoria
        final byte[][] archivoEnMemoria = {null};
        final String[] nombreArchivo = {null};

        // Componente de subida de Vaadin
        com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
        upload.setAcceptedFileTypes("application/pdf");
        upload.setMaxFiles(1);

        // Configuramos el receptor de bytes
        com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer = new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
        upload.setReceiver(buffer);

        Button btnConfirmarRadicacion = new Button("Confirmar Radicación", VaadinIcon.DATABASE.create());
        btnConfirmarRadicacion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnConfirmarRadicacion.setEnabled(false); // 🔒 Bloqueado al inicio hasta que suba el archivo

        // Listener de éxito de carga
        upload.addSucceededListener(event -> {
            try {
                archivoEnMemoria[0] = buffer.getInputStream().readAllBytes();
                nombreArchivo[0] = event.getFileName();
                btnConfirmarRadicacion.setEnabled(true); // 🔓 Desbloqueamos el botón final
                Notification.show("Acta procesada correctamente.", 2000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error al leer el archivo: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Si borra el archivo cargado, volvemos a bloquear
        upload.addFileRejectedListener(e -> {
            archivoEnMemoria[0] = null;
            nombreArchivo[0] = null;
            btnConfirmarRadicacion.setEnabled(false);
        });

        btnConfirmarRadicacion.addClickListener(click -> {
            try {
                //ejecuta la lógica transaccional segura pasándole el PDF
                var emp = empresaService.aprobarRadicacionFinal(solicitud.getId(), archivoEnMemoria[0], nombreArchivo[0]);

                Notification.show("¡Empresa Radicada de forma definitiva! Acta guardada.", 4000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                dialog.close();
                filtrarGrilla(); //actualizams la grilla principal del administrador
            } catch (Exception ex) {
                Notification.show("Error al radicar: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(btnCancelar, btnConfirmarRadicacion);
        layoutModal.add(instruccion, upload);
        dialog.add(layoutModal);
        dialog.open();
    }

    private void filtrarGrilla() {
        List<SolicitudRadicacion> resultado = solicitudRepo.findAll().stream().filter(s -> {
            switch (pestañaActual) {
                case "FASE1":
                    return s.getEstado() == EstadoSolicitud.PENDIENTE || s.getEstado() == EstadoSolicitud.EN_EVALUACION;
                case "FASE2":
                    return s.getEstado() == EstadoSolicitud.DOCUMENTACION_ENVIADA || s.getEstado() == EstadoSolicitud.PRE_APROBADO;
                case "APROBADA":
                    return s.getEstado() == EstadoSolicitud.APROBADA;
                case "RECHAZADA":
                    return s.getEstado() == EstadoSolicitud.RECHAZADA;
                default:
                    return false;
            }
        }).toList();
        grid.setItems(resultado);
    }
}