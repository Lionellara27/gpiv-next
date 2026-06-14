package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.HistorialRadicacion;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.HistorialService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Historial de Radicaciones | SGPIV")
@Route(value = "admin/historiales/radicaciones", layout = MainLayout.class)
public class HistorialRadicacionesView extends VerticalLayout {

    //le inyecto empresaService al constructor para recuperar los PDFs reales de la BD
    public HistorialRadicacionesView(HistorialService historialService, EmpresaService empresaService) {
        setSizeFull();
        setPadding(true);

        Button btnVolver = new Button("Volver al Panel", VaadinIcon.ARROW_LEFT.create(), e ->
                getUI().ifPresent(ui -> ui.navigate(HistorialDashboardView.class))
        );

        HorizontalLayout header = new HorizontalLayout(new H2("Historial Cronológico de Tierras"), btnVolver);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, header.getChildren().findFirst().orElse(null));

        Grid<HistorialRadicacion> grid = new Grid<>(HistorialRadicacion.class, false);
        grid.setSizeFull();

        grid.addColumn(HistorialRadicacion::getRazonSocialEmpresa).setHeader("Empresa").setSortable(true);
        grid.addColumn(HistorialRadicacion::getCuitEmpresa).setHeader("CUIT");
        grid.addColumn(HistorialRadicacion::getNomenclaturaLote).setHeader("Espacio Físico").setSortable(true);
        grid.addColumn(HistorialRadicacion::getFechaAsignacion).setHeader("Fecha Entrada").setSortable(true);

        grid.addComponentColumn(reg -> {
            if (reg.getFechaDesasignacion() == null) {
                Span activo = new Span("Activo Actual");
                activo.getElement().getThemeList().add("badge success");
                return activo;
            } else {
                return new Span(reg.getFechaDesasignacion().toString());
            }
        }).setHeader("Fecha Salida / Liberación").setSortable(true);

        // columna de expediente electronico, las actas
        grid.addComponentColumn(reg -> {
            HorizontalLayout layoutBotones = new HorizontalLayout();
            layoutBotones.setSpacing(true);

            //busca la empresa activa o histórica por CUIT para extraer sus actas guardadas
            var empresaOpt = empresaService.listarTodasLasAprobadas().stream()
                    .filter(emp -> emp.getCuit() != null && emp.getCuit().equals(reg.getCuitEmpresa()))
                    .findFirst();

            if (empresaOpt.isPresent()) {
                var empresa = empresaOpt.get();

                //Botón para el Acta de Radicación (Fase 2)
                if (empresa.getPdfActaRadicacion() != null) {
                    com.vaadin.flow.server.StreamResource resRad = new com.vaadin.flow.server.StreamResource(
                            empresa.getNombreActaRadicacion() != null ? empresa.getNombreActaRadicacion() : "acta_radicacion.pdf",
                            () -> new java.io.ByteArrayInputStream(empresa.getPdfActaRadicacion())
                    );
                    resRad.setContentType("application/pdf");

                    Anchor anchorRad = new Anchor(resRad, "");
                    anchorRad.getElement().setAttribute("download", true);

                    Button btnRad = new Button(VaadinIcon.DOWNLOAD.create());
                    btnRad.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
                    btnRad.setTooltipText("Descargar Acta de Radicación");

                    anchorRad.add(btnRad);
                    layoutBotones.add(anchorRad);
                }

                //Botón para el Acta de Desadjudicación
                if (empresa.getPdfActaDesadjudicacion() != null) {
                    com.vaadin.flow.server.StreamResource resDes = new com.vaadin.flow.server.StreamResource(
                            empresa.getNombreActaDesadjudicacion() != null ? empresa.getNombreActaDesadjudicacion() : "acta_desadjudicacion.pdf",
                            () -> new java.io.ByteArrayInputStream(empresa.getPdfActaDesadjudicacion())
                    );
                    resDes.setContentType("application/pdf");

                    Anchor anchorDes = new Anchor(resDes, "");
                    anchorDes.getElement().setAttribute("download", true);

                    Button btnDes = new Button(VaadinIcon.FILE_REMOVE.create());
                    btnDes.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                    btnDes.setTooltipText("Descargar Acta de Desadjudicación");

                    anchorDes.add(btnDes);
                    layoutBotones.add(anchorDes);
                }
            }

            // Si la empresa no tiene archivos cargados aún en sus slots de actas
            if (layoutBotones.getChildren().count() == 0) {
                Span sinDocumentos = new Span("Sin documentos");
                sinDocumentos.getStyle().set("color", "#A0AEC0").set("font-size", "0.85em");
                layoutBotones.add(sinDocumentos);
            }

            return layoutBotones;
        }).setHeader("Documentos de Respaldo").setAutoWidth(true);

        // =====================================================================

        grid.setItems(historialService.listarTodo());
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        add(header, grid);
    }
}