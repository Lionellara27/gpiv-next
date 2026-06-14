package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.repository.SolicitudRadicacionRepository;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@Route("admin/solicitudes")
public class AdminSolicitudesView extends VerticalLayout {

    private Grid<SolicitudRadicacion> grid = new Grid<>(SolicitudRadicacion.class, false);

    public AdminSolicitudesView(@Autowired SolicitudRadicacionRepository repository, @Autowired EmpresaService service) {
        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("Solicitudes de Radicación Pendientes");

        // --- CONFIGURACIÓN DE LA TABLA (GRID) ---
        grid.addColumn(s -> s.getRepresentante().getNombreCompleto()).setHeader("Representante");
        grid.addColumn(SolicitudRadicacion::getRazonSocialPretendida).setHeader("Empresa Pretendida");
        grid.addColumn(s -> s.getProyecto().getActividadPrincipal()).setHeader("Actividad");

        // Columna para descargar el PDF
        grid.addComponentColumn(solicitud -> {
            if (solicitud.getProyecto().getPdfProyecto() != null) {
                StreamResource resource = new StreamResource(solicitud.getProyecto().getNombreArchivoPdf(),
                        () -> new ByteArrayInputStream(solicitud.getProyecto().getPdfProyecto()));
                Anchor link = new Anchor(resource, "Descargar PDF");
                link.getElement().setAttribute("download", true);
                return link;
            }
            return new com.vaadin.flow.component.html.Span("Sin PDF");
        }).setHeader("Documentación");

        // Botón para Aprobar
        grid.addComponentColumn(solicitud -> {
            Button btnAprobar = new Button("Aprobar", e -> {
                service.aprobarRadicacion(solicitud.getId());
                Notification.show("Empresa radicada con éxito");
                actualizarTabla(repository);
            });
            btnAprobar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            return btnAprobar;
        }).setHeader("Acciones");

        actualizarTabla(repository);

        add(titulo, grid);
    }

    private void actualizarTabla(SolicitudRadicacionRepository repository) {
        grid.setItems(repository.findAll());
    }
}