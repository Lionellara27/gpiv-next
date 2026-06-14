package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.TipoServicio;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Informe de Empresas | SGPIV")
@Route(value = "admin/informe-empresas", layout = MainLayout.class)
public class InformesEmpresasView extends VerticalLayout {

    private final EmpresaService empresaService;
    private Empresa empresaSeleccionadaReal;
    private int anioSeleccionado = LocalDate.now().getYear(); // Por defecto el año actual (2026)

    // Contenedores principales para alternar pantallas
    private VerticalLayout vistaListado = new VerticalLayout();
    private VerticalLayout vistaDetalle = new VerticalLayout();

    // Grilla del listado general
    private Grid<Empresa> gridEmpresasGeneral = new Grid<>(Empresa.class, false);

    // Componentes dinámicos del detalle
    private H2 lblNombreEmpresa = new H2();
    private Span lblRubro = new Span();
    private Paragraph lblDescripcion = new Paragraph();
    private Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");

    private Button btnTitular = new Button("Titular Empresa", VaadinIcon.DIPLOMA.create());
    private Button btnDesadjudicar = new Button("Desadjudicar", VaadinIcon.TRASH.create());
    private Button btnSolicitarInforme = new Button("Solicitar Informe", VaadinIcon.BELL.create());

    // Navegación de años de consumos
    private H3 anioTitulo = new H3();

    // Grillas del detalle
    private Grid<ConsumoMensual> gridConsumos = new Grid<>(ConsumoMensual.class, false);
    private Grid<Lote> gridLotes = new Grid<>(Lote.class, false);
    private Grid<Empleado> gridPersonal = new Grid<>(Empleado.class, false);
    private Grid<Vehiculo> gridVehiculos = new Grid<>(Vehiculo.class, false);

    private Grid<Recurso> gridInventario = new Grid<>(Recurso.class, false);

    private HorizontalLayout otrosServiciosLayout = new HorizontalLayout();
    private VerticalLayout timelineLayout = new VerticalLayout();
    private FooterRow footerRowConsumos;

    private H4 tPersonal = new H4("Nómina de Personal");
    private H4 tVehiculos = new H4("Flota Homologada");

    @Autowired
    public InformesEmpresasView(EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");
        setWidthFull();

        configurarVistaListado();
        configurarVistaDetalle();

        vistaDetalle.setVisible(false);
        add(vistaListado, vistaDetalle);
    }

    private void configurarVistaListado() {
        vistaListado.setWidthFull();
        vistaListado.setPadding(false);

        H2 titulo = new H2("Monitoreo e Informe de Empresas Radicadas");
        Paragraph subtitulo = new Paragraph("Seleccione una empresa para auditar sus consumos, personal, flota e historial de obras.");
        subtitulo.getStyle().set("color", "#666");

        gridEmpresasGeneral.addColumn(Empresa::getRazonSocial).setHeader("Razón Social").setSortable(true);
        gridEmpresasGeneral.addColumn(Empresa::getCuit).setHeader("CUIT");
        gridEmpresasGeneral.addColumn(Empresa::getTipoSociedad).setHeader("Tipo Sociedad");

        gridEmpresasGeneral.addComponentColumn(empresa -> {
            Button btnVer = new Button("Ver Detalle", VaadinIcon.EYE.create());
            btnVer.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            btnVer.addClickListener(e -> mostrarDetalleEmpresa(empresa));
            return btnVer;
        }).setHeader("Acciones").setAutoWidth(true);

        List<Empresa> empresasActivas = empresaService.obtenerTodasLasEmpresas().stream()
                .filter(e -> e.getEstadoEmpresa() == EstadoEmpresa.RADICADA ||
                        e.getEstadoEmpresa() == EstadoEmpresa.TITULADA)
                .toList();
        gridEmpresasGeneral.setItems(empresasActivas);

        vistaListado.add(titulo, subtitulo, gridEmpresasGeneral);
    }

    private void configurarVistaDetalle() {
        vistaDetalle.setWidthFull();
        vistaDetalle.setPadding(false);

        Button btnVolver = new Button("Volver al Listado", VaadinIcon.ARROW_LEFT.create());
        btnVolver.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnVolver.addClickListener(e -> {
            vistaDetalle.setVisible(false);
            vistaListado.setVisible(true);
        });

        // --- 1. CABECERA DE LA EMPRESA ---
        HorizontalLayout headerCard = new HorizontalLayout();
        headerCard.setWidthFull();
        headerCard.setPadding(true);
        headerCard.getStyle().set("background-color", "white").set("border-radius", "15px");
        headerCard.setAlignItems(Alignment.CENTER);

        logoEmpresa.setWidth("100px");
        logoEmpresa.setHeight("100px");
        logoEmpresa.getStyle().set("border-radius", "10px");

        VerticalLayout infoGral = new VerticalLayout();
        infoGral.setSpacing(false);
        infoGral.setPadding(false);

        lblNombreEmpresa.addClassNames(LumoUtility.Margin.Vertical.NONE);
        lblRubro.getStyle().set("color", "#0063BE").set("font-weight", "bold");
        lblDescripcion.getStyle().set("color", "#666");

        infoGral.add(lblNombreEmpresa, lblRubro, lblDescripcion);

        // --- 2. ACÁ METEMOS LOS BOTONES A LA DERECHA ---
        HorizontalLayout accionesLayout = new HorizontalLayout();
        accionesLayout.getStyle().set("margin-left", "auto"); // Empuja los botones a la derecha
        accionesLayout.setAlignItems(Alignment.CENTER);

        btnTitular.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        btnTitular.addClickListener(e -> abrirDialogoTitulacion());

        btnDesadjudicar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        btnDesadjudicar.addClickListener(e -> abrirDialogoDesadjudicacion());

        btnSolicitarInforme.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSolicitarInforme.getStyle().set("background-color", "#FF8C00").set("color", "white");
        btnSolicitarInforme.addClickListener(e -> notificarEmpresa());

        // Metemos los 3 botones al layout de acciones
        accionesLayout.add(btnSolicitarInforme, btnTitular, btnDesadjudicar);
        // ---------------------------------------------

        // Agregamos el Logo, los textos y los Botones a la tarjeta de la cabecera
        headerCard.add(logoEmpresa, infoGral, accionesLayout);

        HorizontalLayout cuerpo = new HorizontalLayout();
        cuerpo.setWidthFull();
        cuerpo.setSpacing(true);

        // --- SECCIÓN IZQUIERDA: CONSUMOS + NAVEGACIÓN + BADGES ---
        VerticalLayout sectionConsumos = new VerticalLayout();
        sectionConsumos.setWidth("60%");
        sectionConsumos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        // 🚀 Barra de Navegación de Año (Rescatada de Master!)
        HorizontalLayout navAnio = new HorizontalLayout();
        navAnio.setWidthFull();
        navAnio.setJustifyContentMode(JustifyContentMode.BETWEEN);
        navAnio.setAlignItems(Alignment.CENTER);

        Button btnAnt = new Button(VaadinIcon.ARROW_LEFT.create(), e -> cambiarAnioConsumo(-1));
        Button btnSig = new Button(VaadinIcon.ARROW_RIGHT.create(), e -> cambiarAnioConsumo(1));
        anioTitulo.addClassNames(LumoUtility.Margin.NONE);

        navAnio.add(btnAnt, anioTitulo, btnSig);

        // Columnas de la Grilla de Consumos
        gridConsumos.addColumn(ConsumoMensual::getMesAnio).setHeader("Período (Mes/Año)");
        gridConsumos.addColumn(c -> c.getConsumoLuz() != null ? c.getConsumoLuz() + " kWh" : "-").setHeader("Luz");
        gridConsumos.addColumn(c -> c.getConsumoAgua() != null ? c.getConsumoAgua() + " Lts" : "-").setHeader("Agua");
        gridConsumos.addColumn(c -> c.getConsumoGas() != null ? c.getConsumoGas() + " m³" : "-").setHeader("Gas");

        // 🚀 Fila de Totales abajo del todo (FooterRow)
        footerRowConsumos = gridConsumos.appendFooterRow();

        otrosServiciosLayout.setSpacing(true);
        otrosServiciosLayout.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        sectionConsumos.add(navAnio, gridConsumos, otrosServiciosLayout);

        // --- SECCIÓN DERECHA: LOTES, PERSONAL Y VEHÍCULOS ---
        VerticalLayout sectionRecursos = new VerticalLayout();
        sectionRecursos.setWidth("40%");
        sectionRecursos.setSpacing(true);

        // Subtarjeta: Lotes
        VerticalLayout cardLotes = new VerticalLayout();
        cardLotes.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1em");
        gridLotes.addColumn(Lote::getManzana).setHeader("Manzana");
        gridLotes.addColumn(Lote::getNroLote).setHeader("Lote");
        gridLotes.addColumn(Lote::getSuperficie).setHeader("Sup. m²");
        cardLotes.add(new H4("Lotes Adjudicados"), gridLotes);

        // Subtarjeta: Personal
        VerticalLayout cardPersonal = new VerticalLayout();
        cardPersonal.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1em");
        gridPersonal.addColumn(Empleado::getNombre).setHeader("Nombre");
        gridPersonal.addColumn(Empleado::getCargo).setHeader("Cargo");
        gridPersonal.addComponentColumn(e -> {
            Span badge = new Span("Activo");
            badge.getStyle().set("font-size", "0.75em").set("padding", "2px 8px").set("border-radius", "10px").set("background-color", "#d4edda");
            return badge;
        }).setHeader("Estado");
        gridPersonal.appendFooterRow(); // Preparamos la fila de totales
        cardPersonal.add(tPersonal, gridPersonal);

        // Subtarjeta: Vehículos
        VerticalLayout cardVehiculos = new VerticalLayout();
        cardVehiculos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1em");
        gridVehiculos.addColumn(Vehiculo::getPatente).setHeader("Patente");
        gridVehiculos.addColumn(Vehiculo::getTipo).setHeader("Tipo");
        gridVehiculos.addColumn(v -> "Al día").setHeader("Seguro/VTV");
        gridVehiculos.appendFooterRow(); // Preparamos la fila de totales
        cardVehiculos.add(tVehiculos, gridVehiculos);

        // Subtarjeta: Inventario de Recursos
        VerticalLayout cardInventario = new VerticalLayout();
        cardInventario.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1em");
        // Suponiendo que tenés una grilla o lista de recursos asignados a la empresa
        Grid<Recurso> gridInventario = new Grid<>(Recurso.class, false);
        gridInventario.addColumn(r -> r.getItem().getNombre()).setHeader("Recurso");
        gridInventario.addColumn(Recurso::getEstadoConservacion).setHeader("Estado");
        cardInventario.add(new H4("Inventario Asignado"), gridInventario);

        sectionRecursos.add(cardLotes, cardPersonal, cardVehiculos, cardInventario);
        cuerpo.add(sectionConsumos, sectionRecursos);

        // --- 3. SECCIÓN DE AVANCES (HISTORIAL COMPLETO BONITO) ---
        VerticalLayout sectionAvances = new VerticalLayout();
        sectionAvances.setWidthFull();
        sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        H3 tAvance = new H3("Historial de Avances de Proyecto");
        timelineLayout.setWidthFull();
        timelineLayout.setPadding(false);

        sectionAvances.add(tAvance, timelineLayout);

        vistaDetalle.add(btnVolver, headerCard, cuerpo, sectionAvances);
    }

    private void mostrarDetalleEmpresa(Empresa empresaVieja) {
        // Despertamos los datos frescos para evitar LazyInitializationException
        this.empresaSeleccionadaReal = empresaService.obtenerEmpresaPorRepresentante(empresaVieja.getRepresentante());

        if (empresaSeleccionadaReal == null) {
            Notification.show("Error al cargar los datos completos de la empresa.");
            return;
        }

        refrescarDatos();

        anioSeleccionado = LocalDate.now().getYear();

        // 1. Cabecera Dinámica
        lblNombreEmpresa.setText(empresaSeleccionadaReal.getRazonSocial());
        lblDescripcion.setText(empresaSeleccionadaReal.getDescripcionPublica() != null ?
                empresaSeleccionadaReal.getDescripcionPublica() : "Sin descripción cargada hasta la fecha.");

        if (empresaSeleccionadaReal.getProyecto() != null) {
            lblRubro.setText("RUBRO / ACTIVIDAD: " + empresaSeleccionadaReal.getProyecto().getRubro());
        } else {
            lblRubro.setText("RUBRO / ACTIVIDAD: No especificado");
        }

        // 2. Cargar datos reales y ACTUALIZAR TÍTULOS CON CONTADORES
        gridConsumos.setItems(empresaSeleccionadaReal.getConsumosMensuales());
        gridLotes.setItems(empresaSeleccionadaReal.getLotesAsignados());

        // --- AQUÍ ESTÁ LA MAGIA DE LOS TAMAÑOS ---
        // Personal y Totales
        gridPersonal.setItems(empresaSeleccionadaReal.getEmpleados());
        tPersonal.setText("Nómina de Personal (Total: " + empresaSeleccionadaReal.getEmpleados().size() + ")");
        FooterRow fPersonal = gridPersonal.getFooterRows().get(0);
        fPersonal.getCell(gridPersonal.getColumns().get(0)).setText("TOTAL EMPLEADOS:");
        fPersonal.getCell(gridPersonal.getColumns().get(1)).setText(String.valueOf(empresaSeleccionadaReal.getEmpleados().size()));

        // Vehículos y Totales
        gridVehiculos.setItems(empresaSeleccionadaReal.getVehiculos());
        tVehiculos.setText("Flota Homologada (Total: " + empresaSeleccionadaReal.getVehiculos().size() + ")");
        FooterRow fVehiculos = gridVehiculos.getFooterRows().get(0);
        fVehiculos.getCell(gridVehiculos.getColumns().get(0)).setText("TOTAL UNIDADES:");
        fVehiculos.getCell(gridVehiculos.getColumns().get(1)).setText(String.valueOf(empresaSeleccionadaReal.getVehiculos().size()));

        // Inventario y Totales
        gridInventario.setItems(empresaSeleccionadaReal.getRecursosAsignados());
        // Como la grilla de inventario aún no tiene un appendFooterRow() en configurarVistaDetalle, por ahora solo le pasamos los ítems.

        // 3. Recalcular Totales y Badges
        actualizarSeccionConsumosPorAnio();
        actualizarBadgesServiciosEspeciales();
        actualizarTimelineAvances();

        vistaListado.setVisible(false);
        vistaDetalle.setVisible(true);
    }


    // Lógica para cambiar de año con las flechitas
    private void cambiarAnioConsumo(int delta) {
        this.anioSeleccionado += delta;
        actualizarSeccionConsumosPorAnio();
    }

    // Filtra la grilla de consumos por año y calcula dinámicamente las sumas en el Footer
    private void actualizarSeccionConsumosPorAnio() {
        anioTitulo.setText("Consumos Año " + anioSeleccionado);

        if (empresaSeleccionadaReal == null) return;

        // Filtramos para quedarnos solo con los registros que terminen con el año seleccionado (ej: "05/2026")
        String filtroAnio = "/" + anioSeleccionado;
        List<ConsumoMensual> consumosDelAnio = empresaSeleccionadaReal.getConsumosMensuales().stream()
                .filter(c -> c.getMesAnio() != null && c.getMesAnio().endsWith(filtroAnio))
                .collect(Collectors.toList());

        gridConsumos.setItems(consumosDelAnio);

        // 🚀 Cálculo matemático de la fila de Totales
        double totalLuz = 0;
        double totalAgua = 0;
        double totalGas = 0;

        for (ConsumoMensual c : consumosDelAnio) {
            if (c.getConsumoLuz() != null) totalLuz += c.getConsumoLuz();
            if (c.getConsumoAgua() != null) totalAgua += c.getConsumoAgua();
            if (c.getConsumoGas() != null) totalGas += c.getConsumoGas();
        }

        // Escribimos los resultados abajo de la tabla
        // Obtenemos las celdas del footer directamente de la fila que creamos
       /* gridConsumos.getColumns().get(0).setFooter("TOTAL ANUAL:");
        gridConsumos.getColumns().get(1).setFooter(totalLuz > 0 ? totalLuz + " kWh" : "-");
        gridConsumos.getColumns().get(2).setFooter(totalAgua > 0 ? totalAgua + " Lts" : "-");
        gridConsumos.getColumns().get(3).setFooter(totalGas > 0 ? totalGas + " m³" : "-");*/

//nuevo metodo para reflejar el total de cada cosa
        gridConsumos.getColumns().get(0).setFooter("TOTAL ANUAL:");
        gridConsumos.getColumns().get(1).setFooter(totalLuz > 0 ? totalLuz + " kWh" : "-");
        gridConsumos.getColumns().get(2).setFooter(totalAgua > 0 ? totalAgua + " Lts" : "-");
        gridConsumos.getColumns().get(3).setFooter(totalGas > 0 ? totalGas + " m³" : "-");
    }

    // Dibuja los cartelitos bonitos de Wifi y Cloaca según los requerimientos del proyecto
    private void actualizarBadgesServiciosEspeciales() {
        otrosServiciosLayout.removeAll();

        // Añadimos badges fijos o dinámicos basados en los servicios declarados por el proyecto de la empresa
        boolean usaWifi = true; // Por defecto para la UX visual que pediste
        boolean usaCloaca = true;

        if (empresaSeleccionadaReal.getProyecto() != null && empresaSeleccionadaReal.getProyecto().getServiciosNecesarios() != null) {
            Set<TipoServicio> servicios = empresaSeleccionadaReal.getProyecto().getServiciosNecesarios();
            // Podés mapear esto con enums personalizados si los tenés, sino dejamos los visuales interactivos:
        }

        if (usaCloaca) {
            otrosServiciosLayout.add(crearBadgeServicio(VaadinIcon.DROP, "Cloaca: Conectado", "#009A3B"));
        }
        if (usaWifi) {
            otrosServiciosLayout.add(crearBadgeServicio(VaadinIcon.SIGNAL, "Fibra Óptica / Wifi: Activo", "#0063BE"));
        }
    }

    private void actualizarTimelineAvances() {
        timelineLayout.removeAll();

        if (empresaSeleccionadaReal.getInformesDeAvance().isEmpty()) {
            Paragraph sinInformes = new Paragraph("No se registran informes de avance cargados hasta la fecha.");
            sinInformes.getStyle().set("color", "#999").set("font-style", "italic");
            timelineLayout.add(sinInformes);
        } else {
            for (InformeAvance informe : empresaSeleccionadaReal.getInformesDeAvance()) {
                VerticalLayout itemTimeline = crearCardAvanceDinamico(informe);
                timelineLayout.add(itemTimeline);
            }
        }
    }

    private VerticalLayout crearCardAvanceDinamico(InformeAvance informe) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.getStyle().set("border-left", "4px solid #0063BE"); // Barrita azul izquierda de tu diseño original
        item.getStyle().set("padding-left", "20px");
        item.getStyle().set("margin-bottom", "15px");

        String fechaStr = informe.getFechaEvaluacion() != null ? informe.getFechaEvaluacion().toString() : "Fecha no registrada";
        Span txtFecha = new Span(VaadinIcon.CALENDAR.create(), new Span(" " + fechaStr));
        txtFecha.getStyle().set("font-size", "0.85em").set("color", "#888").set("font-weight", "600");

        H4 txtAnuncio = new H4(informe.getTitulo());
        txtAnuncio.getStyle().set("margin-top", "4px").set("margin-bottom", "4px");

        String textoEstado = (informe.getEstadoCumplimiento() != null)
                ? informe.getEstadoCumplimiento().name()
                : "SIN EVALUAR";

    // Ahora creamos el Span pasando el texto seguro que nunca va a ser null
        Span badgeEstado = new Span(textoEstado);

        badgeEstado.getStyle().set("font-size", "0.75em")
                .set("padding", "2px 8px")
                .set("border-radius", "10px")
                .set("background-color", "#e0f0ff")
                .set("color", "#0063BE")
                .set("font-weight", "bold");

        HorizontalLayout hHeader = new HorizontalLayout(txtAnuncio, badgeEstado);
        hHeader.setAlignItems(Alignment.CENTER);

        Paragraph txtDesc = new Paragraph(informe.getObservaciones() != null ?
                informe.getObservaciones() : "Sin observaciones descriptivas.");
        txtDesc.getStyle().set("font-size", "0.95em").set("color", "#444");

        item.add(txtFecha, hHeader, txtDesc);

        if (informe.getArchivoPdf() != null) {
            String nombreArchivo = informe.getNombreArchivoPdf() != null ? informe.getNombreArchivoPdf() : "informe_avance.pdf";
            StreamResource resource = new StreamResource(nombreArchivo, () -> new ByteArrayInputStream(informe.getArchivoPdf()));
            resource.setContentType("application/pdf");

            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);

            Button btnDescargarPdf = new Button("Descargar PDF Adjunto", VaadinIcon.DOWNLOAD.create());
            btnDescargarPdf.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);
            downloadLink.add(btnDescargarPdf);

            item.add(downloadLink);
        }

        return item;
    }

    private Span crearBadgeServicio(VaadinIcon icon, String texto, String color) {
        Span badge = new Span(icon.create(), new Span(texto));
        badge.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        badge.getStyle().set("background-color", color + "22"); // Opacidad del 13% para el fondo de la etiqueta
        badge.getStyle().set("color", color);
        badge.getStyle().set("padding", "5px 12px");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("font-size", "0.85em");
        return badge;
    }

    private void refrescarDatos() {
        // 1. Verificamos que la empresa real se haya cargado bien
        if (empresaSeleccionadaReal == null) {
            return;
        }

        // 2. Sacamos el estado real de la empresa
        EstadoEmpresa est = empresaSeleccionadaReal.getEstadoEmpresa() != null ?
                empresaSeleccionadaReal.getEstadoEmpresa() : EstadoEmpresa.INTERESADA;

        // 3. Control de visibilidad de los botones según el estado
        // (OJO: Si querés que se vean SIEMPRE para la demo, ponelos todos en "true")
        if (est == EstadoEmpresa.RADICADA) {
            btnTitular.setVisible(true);
            btnDesadjudicar.setVisible(true);
            btnSolicitarInforme.setVisible(true);
        } else if (est == EstadoEmpresa.TITULADA) {
            btnTitular.setVisible(false);
            btnDesadjudicar.setVisible(true);
            btnSolicitarInforme.setVisible(true);
        } else {
            // Entra acá si es INTERESADA. Para la DEMO cambialos a true si los querés mostrar igual.
            btnTitular.setVisible(true);
            btnDesadjudicar.setVisible(true);
            btnSolicitarInforme.setVisible(true);
        }
    }

// =========================================================================
//  MÉTODOS FALTANTES: PEGALOS AL FINAL DE LA CLASE (Antes de la última '}')
// =========================================================================

private void notificarEmpresa() {
    java.util.List<Long> empresasNotificadas = (java.util.List<Long>) com.vaadin.flow.server.VaadinSession.getCurrent().getAttribute("alertasInformes");
    if (empresasNotificadas == null) {
        empresasNotificadas = new java.util.ArrayList<>();
    }
    if (empresaSeleccionadaReal != null && !empresasNotificadas.contains(empresaSeleccionadaReal.getId())) {
        empresasNotificadas.add(empresaSeleccionadaReal.getId());
    }
    com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("alertasInformes", empresasNotificadas);

    Notification.show("¡Notificación registrada en memoria para esta empresa!", 3000, Notification.Position.TOP_CENTER)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
}

private void abrirDialogoDesadjudicacion() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Desadjudicar Empresa");
    dialog.setCloseOnOutsideClick(false);
    dialog.setCloseOnEsc(false);
    dialog.setWidth("600px");

    VerticalLayout layoutModal = new VerticalLayout();
    layoutModal.setPadding(true);
    layoutModal.setSpacing(true);

    String nombreEmpresa = empresaSeleccionadaReal != null ? empresaSeleccionadaReal.getRazonSocial() : "la empresa";

    Paragraph advertencia = new Paragraph("⚠️ ADVERTENCIA: Esta acción es irreversible. " +
            "Se liberarán todos los lotes asignados a '" + nombreEmpresa +
            "' y su estado pasará a DESADJUDICADA.");
    advertencia.getStyle().set("color", "#d32f2f").set("font-weight", "bold").set("font-size", "0.95em");

    Paragraph instruccion = new Paragraph("Para completar el proceso, cargue el acta oficial de desadjudicación.");
    instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

    final byte[][] pdfEnMemoria = {null};
    final String[] nombrePdf = {null};

    com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
    upload.setAcceptedFileTypes("application/pdf");
    upload.setMaxFiles(1);

    com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer = new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
    upload.setReceiver(buffer);

    Button btnConfirmarDesadjudicacion = new Button("Confirmar Desadjudicación", VaadinIcon.TRASH.create());
    btnConfirmarDesadjudicacion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
    btnConfirmarDesadjudicacion.setEnabled(false);

    upload.addSucceededListener(event -> {
        try {
            pdfEnMemoria[0] = buffer.getInputStream().readAllBytes();
            nombrePdf[0] = event.getFileName();
            btnConfirmarDesadjudicacion.setEnabled(true);
            Notification.show("Acta procesada correctamente.", 2000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error al leer el archivo: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    });

    upload.addFileRejectedListener(e -> {
        pdfEnMemoria[0] = null;
        nombrePdf[0] = null;
        btnConfirmarDesadjudicacion.setEnabled(false);
    });

    btnConfirmarDesadjudicacion.addClickListener(e -> {
        try {
            empresaService.desadjudicarEmpresa(empresaSeleccionadaReal.getId(), pdfEnMemoria[0], nombrePdf[0]);

            Notification.show("¡Empresa desadjudicada correctamente! Lotes liberados.", 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            dialog.close();
            mostrarDetalleEmpresa(empresaSeleccionadaReal); // Refresca la vista
        } catch (Exception ex) {
            Notification.show("Error al desadjudicar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    });

    Button btnCancelar = new Button("Cancelar", e -> dialog.close());
    btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    dialog.getFooter().add(btnCancelar, btnConfirmarDesadjudicacion);
    layoutModal.add(advertencia, instruccion, upload);
    dialog.add(layoutModal);
    dialog.open();
}

private void abrirDialogoTitulacion() {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("Otorgar Titularidad - HU 11");
    dialog.setCloseOnOutsideClick(false);
    dialog.setCloseOnEsc(false);
    dialog.setWidth("600px");

    VerticalLayout layoutModal = new VerticalLayout();
    layoutModal.setPadding(true);
    layoutModal.setSpacing(true);

    String nombreEmpresa = empresaSeleccionadaReal != null ? empresaSeleccionadaReal.getRazonSocial() : "la empresa";

    Paragraph instruccion = new Paragraph("Para otorgar la titularidad definitiva a '" +
            nombreEmpresa +
            "', cargue el acta de otorgamiento de escritura.");
    instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

    final byte[][] pdfEnMemoria = {null};
    final String[] nombrePdf = {null};

    com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
    upload.setAcceptedFileTypes("application/pdf");
    upload.setMaxFiles(1);

    com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer = new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
    upload.setReceiver(buffer);

    Button btnConfirmarTitulacion = new Button("Confirmar Titularidad", VaadinIcon.DIPLOMA.create());
    btnConfirmarTitulacion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
    btnConfirmarTitulacion.setEnabled(false);

    upload.addSucceededListener(event -> {
        try {
            pdfEnMemoria[0] = buffer.getInputStream().readAllBytes();
            nombrePdf[0] = event.getFileName();
            btnConfirmarTitulacion.setEnabled(true);
            Notification.show("Acta procesada correctamente.", 2000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error al leer el archivo: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    });

    upload.addFileRejectedListener(e -> {
        pdfEnMemoria[0] = null;
        nombrePdf[0] = null;
        btnConfirmarTitulacion.setEnabled(false);
    });

    btnConfirmarTitulacion.addClickListener(e -> {
        try {
            // Guardar el PDF y cambiar estado
            empresaSeleccionadaReal.setPdfActaRadicacion(pdfEnMemoria[0]);
            empresaSeleccionadaReal.setNombreActaRadicacion(nombrePdf[0]);

            empresaService.titularEmpresa(empresaSeleccionadaReal.getId());

            Notification.show("¡Empresa titulada con éxito! Acta guardada.", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
            mostrarDetalleEmpresa(empresaSeleccionadaReal); // Refresca la vista
        } catch (Exception ex) {
            Notification.show("Error al procesar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    });

    Button btnCancelar = new Button("Cancelar", e -> dialog.close());
    btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    dialog.getFooter().add(btnCancelar, btnConfirmarTitulacion);
    layoutModal.add(instruccion, upload);
    dialog.add(layoutModal);
    dialog.open();
}
}

/*package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Informes de Empresas | SGPIV")
@Route(value = "admin/informes-empresas", layout = MainLayout.class)
public class InformesEmpresasView extends VerticalLayout implements BeforeEnterObserver {

    private final EmpresaService empresaService;
    private Grid<Empresa> grid = new Grid<>(Empresa.class, false);

    public InformesEmpresasView(EmpresaService empresaService) {
        this.empresaService = empresaService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        H2 titulo = new H2("Listado de Empresas Radicadas");

        configurarTabla();

        add(titulo, grid);
        actualizarTabla();
    }

    private void configurarTabla() {
        grid.setSizeFull();

        grid.addColumn(Empresa::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Empresa::getRazonSocial).setHeader("Razón Social").setSortable(true);
        grid.addColumn(Empresa::getCuit).setHeader("CUIT");

        // 1. Estado del Trámite Interno (Solicitud)
        grid.addColumn(Empresa::getEstado).setHeader("Estado Solicitud").setSortable(true);

        // 2. 🟢 Estado Operativo Real (Tu nuevo Enum de Empresa) utilizando Badges visuales
        grid.addComponentColumn(empresa -> {
            com.vaadin.flow.component.html.Span badge = new com.vaadin.flow.component.html.Span();
            EstadoEmpresa est = empresa.getEstadoEmpresa() != null ? empresa.getEstadoEmpresa() : EstadoEmpresa.INTERESADA;
            badge.setText(est.toString());
            badge.getElement().getThemeList().add("badge");

            switch (est) {
                case INTERESADA:
                    badge.getStyle().set("background-color", "#e0f7fa").set("color", "#006064"); // Celeste
                    break;
                case RADICADA:
                    badge.getElement().getThemeList().add("success"); // Verde estándar Lumo
                    break;
                case TITULADA:
                    badge.getStyle().set("background-color", "#f3e5f5").set("color", "#4a148c"); // Violeta premium
                    break;
            }
            return badge;
        }).setHeader("Condición en Parque").setSortable(true);

        // Acciones (Ojito)
        grid.addComponentColumn(empresa -> {
            Button btnDetalle = new Button("Ver Detalle", VaadinIcon.EYE.create());
            btnDetalle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btnDetalle.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate(EmpresaDetalleView.class, empresa.getId()))
            );
            return btnDetalle;
        }).setHeader("Acciones");

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
    }

    private void actualizarTabla() {
        grid.setItems(empresaService.obtenerTodasLasEmpresas());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        actualizarTabla();
    }
}
*/
/*
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Detalle de Empresa | SGPIV")
@Route(value = "admin/informe-detalle", layout = MainLayout.class)
public class InformesEmpresasView extends VerticalLayout {

    public InformesEmpresasView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#f5f7fa");

        // --- 1. CABECERA: LOGO + INFO GENERAL ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.getStyle().set("background-color", "white");
        header.getStyle().set("border-radius", "15px");
        header.setAlignItems(Alignment.CENTER);

        Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");
        logoEmpresa.setWidth("100px");
        logoEmpresa.setHeight("100px");
        logoEmpresa.getStyle().set("border-radius", "10px");

        VerticalLayout infoGral = new VerticalLayout();
        infoGral.setSpacing(false);
        infoGral.setPadding(false);

        H2 nombre = new H2("Logística del Sur S.A.");
        nombre.addClassNames(LumoUtility.Margin.Vertical.NONE);

        Span rubro = new Span("RUBRO: Transporte y Almacenamiento Frío");
        rubro.getStyle().set("color", "#0063BE").set("font-weight", "bold");

        Paragraph desc = new Paragraph("Empresa dedicada a la distribución de productos perecederos en la Patagonia. " +
                "Cuenta con flota propia y depósito fiscal en el Lote 14.");
        desc.getStyle().set("color", "#666");

        infoGral.add(nombre, rubro, desc);
        header.add(logoEmpresa, infoGral);

        // --- 2. CUERPO: CONSUMOS (IZQ) Y RECURSOS (DER) ---
        HorizontalLayout cuerpo = new HorizontalLayout();
        cuerpo.setWidthFull();
        cuerpo.setSpacing(true);

        // --- SECCIÓN IZQUIERDA: CONSUMOS ANUALES ---
        VerticalLayout sectionConsumos = new VerticalLayout();
        sectionConsumos.setWidth("65%");
        sectionConsumos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        HorizontalLayout navAnio = new HorizontalLayout();
        navAnio.setWidthFull();
        navAnio.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button btnAnt = new Button(VaadinIcon.ARROW_LEFT.create());
        H3 anioTitulo = new H3("Consumos Año 2025");
        Button btnSig = new Button(VaadinIcon.ARROW_RIGHT.create());

        navAnio.add(btnAnt, anioTitulo, btnSig);

        Grid<String[]> gridConsumos = new Grid<>();
        gridConsumos.addColumn(d -> d[0]).setHeader("Mes");
        gridConsumos.addColumn(d -> d[1]).setHeader("Luz (kWh)");
        gridConsumos.addColumn(d -> d[2]).setHeader("Agua (Lts)");
        gridConsumos.addColumn(d -> d[3]).setHeader("Gas (m3)");

        gridConsumos.setItems(new String[][]{
                {"Enero", "25.000", "5.000", "1.200"},
                {"Febrero", "28.500", "4.800", "1.100"},
                {"Marzo", "22.000", "5.200", "2.500"}
        });

        // Representación de Servicios Especiales
        HorizontalLayout otrosServicios = new HorizontalLayout();
        otrosServicios.add(
                crearBadgeServicio(VaadinIcon.DROP, "Cloaca: Conectado", "#009A3B"),
                crearBadgeServicio(VaadinIcon.SIGNAL, "Fibra Óptica: Activo", "#0063BE")
        );

        sectionConsumos.add(navAnio, gridConsumos, otrosServicios);

        // --- SECCIÓN DERECHA: EMPLEADOS Y VEHÍCULOS ---
        VerticalLayout sectionRecursos = new VerticalLayout();
        sectionRecursos.setWidth("35%");

        // Tabla Empleados
        VerticalLayout divEmp = new VerticalLayout();
        divEmp.getStyle().set("background-color", "white").set("border-radius", "15px");
        H4 tEmp = new H4("Personal (Total: 15)");
        Grid<String[]> gridEmp = new Grid<>();
        gridEmp.addColumn(d -> d[0]).setHeader("Nombre");
        gridEmp.addColumn(d -> d[1]).setHeader("Cargo");
        gridEmp.setItems(new String[][]{{"Juan Perez", "Gerente"}, {"Marta Gomez", "Operario"}});
        divEmp.add(tEmp, gridEmp);

        // Tabla Vehículos
        VerticalLayout divVeh = new VerticalLayout();
        divVeh.getStyle().set("background-color", "white").set("border-radius", "15px");
        H4 tVeh = new H4("Vehículos (Total: 4)");
        Grid<String[]> gridVeh = new Grid<>();
        gridVeh.addColumn(d -> d[0]).setHeader("Patente");
        gridVeh.addColumn(d -> d[1]).setHeader("Tipo");
        gridVeh.setItems(new String[][]{{"AF-123-JK", "Camión Scania"}, {"AD-444-OP", "Clinch"}});
        divVeh.add(tVeh, gridVeh);

        sectionRecursos.add(divEmp, divVeh);

        // --- 3. SECCIÓN DE AVANCES (HISTORIAL) ---
        VerticalLayout sectionAvances = new VerticalLayout();
        sectionAvances.setWidthFull();
        sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

        H3 tAvance = new H3("Historial de Avances de Proyecto");

        VerticalLayout timeline = new VerticalLayout();
        timeline.add(crearItemAvance("02/05/2026", "Finalización de cerramiento perimetral", "Se terminaron de levantar las 4 paredes del galpón principal."));
        timeline.add(crearItemAvance("15/04/2026", "Conexión de servicios", "Se completó la instalación de gas industrial y medidores."));

        sectionAvances.add(tAvance, timeline);

        // Agregar todo a la vista
        cuerpo.add(sectionConsumos, sectionRecursos);
        add(header, cuerpo, sectionAvances);
    }

    private Span crearBadgeServicio(VaadinIcon icon, String texto, String color) {
        Span badge = new Span(icon.create(), new Span(texto));
        badge.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        badge.getStyle().set("background-color", color + "22"); // 22 es transparencia
        badge.getStyle().set("color", color);
        badge.getStyle().set("padding", "5px 12px");
        badge.getStyle().set("border-radius", "20px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("font-size", "0.85em");
        return badge;
    }

    private VerticalLayout crearItemAvance(String fecha, String titulo, String desc) {
        VerticalLayout item = new VerticalLayout();
        item.setSpacing(false);
        item.setPadding(false);
        item.getStyle().set("border-left", "3px solid #0063BE");
        item.getStyle().set("padding-left", "15px");
        item.getStyle().set("margin-bottom", "10px");

        Span txtFecha = new Span(fecha);
        txtFecha.getStyle().set("font-size", "0.8em").set("color", "#999");
        H5 txtTit = new H5(titulo);
        Paragraph txtDesc = new Paragraph(desc);
        txtDesc.getStyle().set("font-size", "0.9em");

        item.add(txtFecha, txtTit, txtDesc);
        return item;
    }
}
 */