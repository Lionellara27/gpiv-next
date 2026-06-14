package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import com.unrn.gpiv.model.InformeAvance;
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.service.InformeAvanceService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.service.RecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
//cosas new
import com.unrn.gpiv.service.EmpresaService; // <--- 1. IMPORTANTE: Importá tu Service
import org.springframework.beans.factory.annotation.Autowired; // <--- 2. Para la inyección

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Dashboard Administrador | SGPIV")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class AdminDashboardView extends VerticalLayout {
    private final EmpresaService empresaService; // <--- 3. Variable para el Service

    private final LoteService loteService;
    private final RecursoService recursoService;
    private final InformeAvanceService informeService;

    public AdminDashboardView(@Autowired EmpresaService empresaService, LoteService loteService, RecursoService recursoService,InformeAvanceService informeService) {
        this.empresaService = empresaService;
        this.loteService = loteService;
        this.recursoService = recursoService;
        this.informeService = informeService;
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        H2 header = new H2("Tablero de Gestión Municipal");

// --- TARJETAS KPI (DINÁMICAS E INTERACTIVAS) ---
        HorizontalLayout kpiLayout = new HorizontalLayout();
        kpiLayout.setWidthFull();
        kpiLayout.setSpacing(true);

        // 1. Pedimos los números reales a los servicios de la base de datos
        long cantLotes = loteService.contarLotesLibres();
        long cantEmpresas = empresaService.contarEmpresasRadicadas();
        long cantPrestamos = recursoService.obtenerCantidadPrestados();
        long cantSolicitudes = empresaService.contarSolicitudesPendientes();

        // 2. Configuración de Tarjeta: SOLICITUDES (Navega a evaluar solicitudes)
        String textoSolicitudes = cantSolicitudes + (cantSolicitudes == 1 ? " PENDIENTE" : " PENDIENTES");
        VerticalLayout cardSolicitudes = crearTarjetaKPI("SOLICITUDES", textoSolicitudes, VaadinIcon.ENVELOPE, "#0072BB");
        cardSolicitudes.getStyle().set("cursor", "pointer"); // Transforma el cursor en manito
        cardSolicitudes.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("admin/evaluar")));

        // 3. Configuración de Tarjeta: LOTES (Navega a gestión de lotes)
        String textoLotes = cantLotes + (cantLotes == 1 ? " LIBRE" : " LIBRES");
        VerticalLayout cardLotes = crearTarjetaKPI("LOTES", textoLotes, VaadinIcon.MAP_MARKER, "#009A3B");
        cardLotes.getStyle().set("cursor", "pointer");
        cardLotes.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("admin/lotes")));

        // 4. Configuración de Tarjeta: EMPRESAS (Navega a informe de empresas)
        String textoEmpresas = cantEmpresas + (cantEmpresas == 1 ? " RADICADA" : " RADICADAS");
        VerticalLayout cardEmpresas = crearTarjetaKPI("EMPRESAS", textoEmpresas, VaadinIcon.FACTORY, "#666");
        cardEmpresas.getStyle().set("cursor", "pointer");
        cardEmpresas.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("admin/informe-empresas")));

        // 5. Configuración de Tarjeta: PRÉSTAMOS (Navega al inventario de recursos)
        String textoPrestamos = cantPrestamos + (cantPrestamos == 1 ? " ACTIVO" : " ACTIVOS");
        VerticalLayout cardPrestamos = crearTarjetaKPI("PRÉSTAMOS", textoPrestamos, VaadinIcon.TOOLS, "#E67E22");
        cardPrestamos.getStyle().set("cursor", "pointer");
        cardPrestamos.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("admin/inventario")));

        // 6. Inyectamos las 4 variables ya configuradas con sus listeners al layout horizontal
        kpiLayout.add(cardSolicitudes, cardLotes, cardEmpresas, cardPrestamos);


        // --- CONTENEDOR DE TABLAS (MITAD Y MITAD) ---
        HorizontalLayout tablasLayout = new HorizontalLayout();
        tablasLayout.setWidthFull();
        tablasLayout.setSpacing(true);

        // Llamamos a los métodos privados que hacen el trabajo sucio
        tablasLayout.add(crearPanelPrestamos(), crearPanelConsumos());

        // Agregamos todo a la vista principal
        add(header, kpiLayout, tablasLayout);
    }

    private VerticalLayout crearTarjetaKPI(String titulo, String valor, VaadinIcon icono, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)");
        card.getStyle().set("border-left", "5px solid " + color);
        card.setAlignItems(Alignment.CENTER);

        Span txtTitulo = new Span(titulo);
        txtTitulo.getStyle().set("font-size", "0.8em").set("color", "#999").set("font-weight", "bold");

        Span txtValor = new Span(valor);
        txtValor.getStyle().set("font-size", "1.5em").set("font-weight", "bold").set("color", color);

        card.add(icono.create(), txtTitulo, txtValor);
        return card;
    }

    private VerticalLayout crearPanelPrestamos() {
        VerticalLayout section = new VerticalLayout();
        section.setWidth("50%");
        section.getStyle().set("background-color", "white");
        section.getStyle().set("border-radius", "15px");
        section.getStyle().set("padding", "1.5em");
        section.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");
        // ... acá pones los estilos que ya tenías ...

        H3 invTitle = new H3("Estado de Recursos y Maquinaria");

        // 1. Grid tipado con la clase Recurso
        Grid<Recurso> grid = new Grid<>(Recurso.class, false);

        // 2. Columnas vinculadas a los métodos del modelo
        grid.addColumn(r -> r.getItem().getNombre()).setHeader("Recurso");
        grid.addColumn(Recurso::getEstadoMovimiento).setHeader("Estado");
        grid.addColumn(r -> {
            if (r.getPropietarioEmpresa() != null &&
                    (r.getEstadoMovimiento() == EstadoMovimientoRecurso.DISPONIBLE || r.getEstadoMovimiento() == EstadoMovimientoRecurso.PAUSADO)) {

                return r.getPropietarioEmpresa().getRazonSocial();
            }

            if (r.getPrestadoA() != null) {
                return r.getPrestadoA().getRazonSocial() + " [En Uso]";
            }

            return "En deposito";
        }).setHeader("Poseedor").setSortable(true);
//        grid.addColumn(r -> r.getPrestadoA() != null ? r.getPrestadoA().getRazonSocial() : "En Pañol").setHeader("Poseedor");

        // 3. Traer datos reales
        List<Recurso> activos = recursoService.obtenerTodoElInventario().stream()
                .filter(r -> r.getEstadoMovimiento() != EstadoMovimientoRecurso.DISPONIBLE)
                .collect(Collectors.toList());
        grid.setItems(activos);

        // 4. Totales calculados con Stream (sin hardcodeo)
        long prestados = activos.stream().filter(r -> r.getEstadoMovimiento() == EstadoMovimientoRecurso.PRESTADO).count();
        long aDevolver = activos.stream().filter(r -> r.getEstadoMovimiento() == EstadoMovimientoRecurso.A_DEVOLVER).count();

        Span totalPrestados = new Span("📤 Prestados: " + prestados);
        Span totalAdevolver = new Span("📥 A devolver: " + aDevolver);

        section.add(invTitle, grid, new HorizontalLayout(totalPrestados, totalAdevolver));
        return section;
    }


    private VerticalLayout crearPanelConsumos() {
        VerticalLayout section = new VerticalLayout();
        section.setWidth("50%");
        section.getStyle().set("background-color", "white")
                .set("border-radius", "15px")
                .set("padding", "1.5em")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        H3 title = new H3("Top Consumos del Mes");

        // 1. Grid tipado con InformeAvance
        Grid<InformeAvance> grid = new Grid<>(InformeAvance.class, false);
        grid.addColumn(i -> i.getEmpresa().getRazonSocial()).setHeader("Empresa");
        grid.addColumn(InformeAvance::getEnergia).setHeader("Energía (kWh)");
        grid.addColumn(InformeAvance::getAgua).setHeader("Agua (Lts)");
        grid.addColumn(InformeAvance::getGas).setHeader("Gas (m³)");

        // 3. Traer datos reales (Necesitarías un método en tu Service para filtrar informes del mes)
        List<InformeAvance> informes = informeService.obtenerInformesMesActual();
        grid.setItems(informes);

        // 4. Totales calculados dinámicamente
        double totalEnergia = informes.stream().mapToDouble(InformeAvance::getEnergia).sum();
        double totalAgua = informes.stream().mapToDouble(InformeAvance::getAgua).sum();
        double totalGas = informes.stream().mapToDouble(InformeAvance::getGas).sum();

        Span spanEnergia = new Span("⚡ Total Energía: " + totalEnergia + " kWh");
        Span spanAgua = new Span("💧 Total Agua: " + totalAgua + " Lts");
        Span spanGas = new Span("🔥 Total Gas: " + totalGas + " m³");

        HorizontalLayout totales = new HorizontalLayout(spanEnergia, spanAgua, spanGas);
        totales.setWidthFull();
        totales.setJustifyContentMode(JustifyContentMode.BETWEEN);

        section.add(title, grid, totales);
        return section;
    }
}
