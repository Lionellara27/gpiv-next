package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Avances de Obra | Parque Industrial")
@Route(value = "avances-obra", layout = MainLayout.class)
public class AvancesObraView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaActual;

    // Contenedores para cada pestaña
    private VerticalLayout tabPerfilYRecursos = new VerticalLayout();
    private VerticalLayout tabConsumos = new VerticalLayout();
    private VerticalLayout tabHistorial = new VerticalLayout();

    public AvancesObraView(EmpresaService empresaService) {
        this.empresaService = empresaService;

        // 1. Obtener la empresa logueada
        RepresentanteEmpresa rep = (RepresentanteEmpresa) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
        if (rep != null) {
            // Le pasamos 'rep' entero, sin el .getId()
            this.empresaActual = empresaService.obtenerEmpresaPorRepresentante(rep);
        }

        // Estilos básicos de la página
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        // 2. Encabezado
        H2 titulo = new H2("Panel de Avance del Proyecto");
        titulo.getStyle().set("color", "#0063BE").set("margin-bottom", "0");
        Paragraph subtitulo = new Paragraph("Gestioná los recursos, consumos e informes de tu empresa en el Parque.");
        subtitulo.getStyle().set("color", "#666").set("margin-top", "0");

        // 3. Crear las Pestañas (Tabs)
        Tab tab1 = new Tab("Perfil y Recursos");
        Tab tab2 = new Tab("Consumos Mensuales");
        Tab tab3 = new Tab("Historial de Avances");

        Tabs tabs = new Tabs(tab1, tab2, tab3);
        tabs.setWidthFull();

        // 4. Configurar los contenedores (solo mostramos el primero al inicio)
        configurarContenedores();
        tabPerfilYRecursos.setVisible(true);
        tabConsumos.setVisible(false);
        tabHistorial.setVisible(false);

        // 5. Lógica para cambiar de pestaña al hacer clic
        tabs.addSelectedChangeListener(event -> {
            tabPerfilYRecursos.setVisible(event.getSelectedTab() == tab1);
            tabConsumos.setVisible(event.getSelectedTab() == tab2);
            tabHistorial.setVisible(event.getSelectedTab() == tab3);
        });

        // 6. Agregar todo a la pantalla principal
        add(titulo, subtitulo, tabs, tabPerfilYRecursos, tabConsumos, tabHistorial);
    }

    private void configurarContenedores() {
        // Estilos para que parezcan "tarjetas" blancas
        String estiloTarjeta = "white";

        // PESTAÑA 1: PERFIL Y RECURSOS
        tabPerfilYRecursos.getStyle().set("background-color", estiloTarjeta).set("padding", "2em").set("border-radius", "10px");

        if (empresaActual != null) {
            ControlPersonalView panelPersonal = new ControlPersonalView(empresaActual, empresaService);
            FlotaVehiculosView panelVehiculos = new FlotaVehiculosView(empresaActual, empresaService);
            tabPerfilYRecursos.add(panelPersonal, panelVehiculos);
        } else {
            tabPerfilYRecursos.add(new H2("No tenés una empresa vinculada."));
        }

        // PESTAÑA 2: CONSUMOS MENSUALE
        tabConsumos.getStyle().set("background-color", estiloTarjeta).set("padding", "2em").set("border-radius", "10px");

        if (empresaActual != null) {
            MedicionConsumosView panelConsumos = new MedicionConsumosView(empresaActual, empresaService);
            tabConsumos.add(panelConsumos);
        } else {
            tabConsumos.add(new H2("No tenés una empresa vinculada."));
        }

        // PESTAÑA 3: HISTORIAL DE AVANCES
        tabHistorial.getStyle().set("background-color", estiloTarjeta).set("padding", "2em").set("border-radius", "10px");
        if (empresaActual != null) {
            HistorialAvancesView panelAvances = new HistorialAvancesView(empresaActual, empresaService);
            tabHistorial.add(panelAvances);
        } else {
            tabHistorial.add(new H2("No tenés una empresa vinculada."));
        }
    }
}