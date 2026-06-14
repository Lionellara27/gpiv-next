package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.SolicitudRecurso;
import com.unrn.gpiv.service.SolicitudRecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;

@PageTitle("Solicitudes de Rucursos | SGPIV")
@Route(value = "admin/solicitudes-recursos", layout = MainLayout.class)
public class SolicitudesRecursosView extends VerticalLayout {

    private final SolicitudRecursoService solicitudService;
    private Grid<SolicitudRecurso> grid = new Grid<>(SolicitudRecurso.class, false);

    public SolicitudesRecursosView(SolicitudRecursoService solicitudService) {
        this.solicitudService = solicitudService;

        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("Solicitudes de Herramientas y Recursos");

        configurarGrid();
        add(titulo, grid);
        actualizarTabla();
    }

    private void configurarGrid() {
        grid.setSizeFull();

        grid.addColumn(s -> s.getFechaSolicitud() != null ? s.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-")
                .setHeader("Fecha").setSortable(true).setWidth("120px").setFlexGrow(0);

        grid.addColumn(s -> s.getEmpresa() != null ? s.getEmpresa().getRazonSocial() : "S/E")
                .setHeader("Solicitante").setSortable(true);

        grid.addColumn(s -> s.getItemSolicitado() != null ? s.getItemSolicitado().getNombre() : "S/N")
                .setHeader("Recurso Pedido").setSortable(true);

        grid.addColumn(SolicitudRecurso::getCantidad).setHeader("Cantidad").setWidth("90px").setFlexGrow(0);

        grid.addColumn(SolicitudRecurso::getMotivo).setHeader("Motivo");

        grid.addComponentColumn(this::crearBotonesAccion).setHeader("Acciones").setWidth("160px").setFlexGrow(0);
    }

    private HorizontalLayout crearBotonesAccion(SolicitudRecurso s) {
        Button btnAprobar = new Button(VaadinIcon.CHECK.create(), e -> procesarAprobacion(s));
        btnAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnAprobar.setTooltipText("Aprobar solicitud y asignar recursos automáticamente");

        Button btnRechazar = new Button(VaadinIcon.CLOSE.create(), e -> abrirModalRechazo(s));
        btnRechazar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        btnRechazar.setTooltipText("Rechazar y comunicar motivo");

        HorizontalLayout hl = new HorizontalLayout(btnAprobar, btnRechazar);
        hl.setSpacing(true);
        return hl;
    }

    private void procesarAprobacion(SolicitudRecurso s) {
        try {
            solicitudService.aprobarSolicitud(s.getId());
            Notification.show("Solicitud aprobada con éxito. Inventario actualizado y empresa notificada.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarTabla();
        } catch (Exception ex) {
            Notification.show("Error al aprobar: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void abrirModalRechazo(SolicitudRecurso s) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rechazar Pedido - " + s.getEmpresa().getRazonSocial());

        TextArea txtMotivo = new TextArea("Escriba el motivo del rechazo");
        txtMotivo.setPlaceholder("Ej: El recurso solicitado no tiene stock...");
        txtMotivo.setWidthFull();
        txtMotivo.setHeight("120px");
        txtMotivo.setRequired(true);

        Button btnConfirmar = new Button("Enviar Rechazo", click -> {
            if (txtMotivo.isEmpty()) {
                Notification.show("Debe ingresar un motivo para informar a la empresa.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                solicitudService.rechazarSolicitud(s.getId(), txtMotivo.getValue());
                Notification.show("Solicitud rechazada. Se envió la notificación correspondiente.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
                actualizarTabla();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(txtMotivo);
        dialog.add(layout);
        dialog.getFooter().add(btnConfirmar, new Button("Cancelar", c -> dialog.close()));
        dialog.open();
    }

    private void actualizarTabla() {
        grid.setItems(solicitudService.listarPendientes());
    }
}