package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Panel de Historiales | SGPIV")
@Route(value = "admin/historiales", layout = MainLayout.class)
public class HistorialDashboardView extends VerticalLayout {

    public HistorialDashboardView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Módulo de Auditoría e Historiales"));

        HorizontalLayout contenedorTarjetas = new HorizontalLayout();
        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.setSpacing(true);

        // --- TARJETA 1: HISTORIAL DE RADICACIONES ---
        VerticalLayout cardRadicaciones = new VerticalLayout();
        cardRadicaciones.setWidth("300px");
        cardRadicaciones.getStyle()
                .set("background-color", "#F8FAFC")
                .set("border", "1px solid #E2E8F0")
                .set("border-radius", "12px")
                .set("padding", "20px");

        H3 tituloCard = new H3("Radicación de Empresas");
        Paragraph descCard = new Paragraph("Consulte el registro histórico de asignaciones, mudanzas y desocupación de lotes en el parque.");

        Button btnAcceder = new Button("Ver Historial", VaadinIcon.EYE.create(), e ->
                getUI().ifPresent(ui -> ui.navigate(HistorialRadicacionesView.class))
        );
        btnAcceder.addThemeNames("primary");

        cardRadicaciones.add(tituloCard, descCard, btnAcceder);
        contenedorTarjetas.add(cardRadicaciones);

        // --- FUTURAS TARJETAS AQUÍ ---
        // Podés clonar el bloque de arriba en el futuro para "Historial de Pagos", etc.

        add(contenedorTarjetas);
    }
}
