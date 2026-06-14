package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoConservacionRecurso;
import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.RecursoService;
import com.unrn.gpiv.service.SolicitudRecursoService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.stream.Collectors;

@PageTitle("Herramientas y Recursos | SGPIV")
@Route(value = "empresa/herramientas-y-recursos", layout = MainLayout.class)
public class SolicitarRecursoView extends VerticalLayout {

    private final SolicitudRecursoService solicitudService;
    private final EmpresaService empresaService;
    private final RecursoService recursoService;

    private Empresa empresaLogueada;

    private Grid<Recurso> gridMisPrestamos = new Grid<>(Recurso.class, false);
    private ComboBox<Item> cmbItems;
    private TextField txtCategoriaPedir;

    private Grid<Recurso> gridMisAportes = new Grid<>(Recurso.class, false);

    public SolicitarRecursoView(SolicitudRecursoService solicitudService, EmpresaService empresaService, RecursoService recursoService) {
        this.solicitudService = solicitudService;
        this.empresaService = empresaService;
        this.recursoService = recursoService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        H2 tituloGral = new H2("Herramientas y Recursos");
        tituloGral.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.SMALL, LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.BOLD);
        add(tituloGral);

        if (!validarAccesoEmpresa()) {
            return;
        }

        Tab tabPedir = new Tab(VaadinIcon.DOWNLOAD.create(), new Span(" Pedir Herramientas"));
        Tab tabOfrecer = new Tab(VaadinIcon.UPLOAD.create(), new Span(" Ofrecer mis Herramientas"));
        Tabs tabs = new Tabs(tabPedir, tabOfrecer);
        tabs.setWidthFull();
        add(tabs);

        VerticalLayout contenedorPedir = crearLayoutPedirHerramientas();
        VerticalLayout contenedorOfrecer = crearLayoutOfrecerHerramientas();

        contenedorOfrecer.setVisible(false);
        add(contenedorPedir, contenedorOfrecer);

        tabs.addSelectedChangeListener(event -> {
            boolean pedirActivo = event.getSelectedTab().equals(tabPedir);
            contenedorPedir.setVisible(pedirActivo);
            contenedorOfrecer.setVisible(!pedirActivo);
            if (pedirActivo) {
                actualizarTablaPrestamos();
            } else {
                actualizarTablaAportes();
            }
        });

        actualizarTablaPrestamos();
    }


    // PESTAÑA 1: PEDIR HERRAMIENTAS
    private VerticalLayout crearLayoutPedirHerramientas() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        VerticalLayout cardForm = new VerticalLayout();
        cardForm.setWidthFull();
        cardForm.setPadding(true);
        cardForm.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.MEDIUM);
        cardForm.getElement().getThemeList().add("shadow-sm");

        H3 subtituloIzquierdo = new H3("📝 Nueva Solicitud de Préstamo");
        subtituloIzquierdo.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL, LumoUtility.Border.BOTTOM, LumoUtility.Padding.Bottom.SMALL);
        cardForm.add(subtituloIzquierdo);

        cmbItems = new ComboBox<>("Seleccione el recurso que necesita *");
        cmbItems.setItemLabelGenerator(Item::getNombre);
        cmbItems.setWidthFull();

        txtCategoriaPedir = new TextField("Categoría del Recurso");
        txtCategoriaPedir.setReadOnly(true);
        txtCategoriaPedir.setPlaceholder("Se autocompleta al elegir el recurso...");
        txtCategoriaPedir.setWidthFull();

        IntegerField txtCantidad = new IntegerField("Cantidad Requerida *");
        txtCantidad.setValue(1);
        txtCantidad.setMin(1);
        txtCantidad.setStepButtonsVisible(true);
        txtCantidad.setWidthFull();

        cmbItems.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                txtCategoriaPedir.setValue(e.getValue().getCategoria());
            } else {
                txtCategoriaPedir.clear();
            }
        });
        refrescarComboItems();

        HorizontalLayout filaFormulario = new HorizontalLayout(cmbItems, txtCategoriaPedir, txtCantidad);
        filaFormulario.setWidthFull();

        TextArea txtMotivo = new TextArea("Motivo de la Solicitud *");
        txtMotivo.setPlaceholder("Describa brevemente para qué tareas de la empresa requiere el recurso...");
        txtMotivo.setRequired(true);
        txtMotivo.setWidthFull();
        txtMotivo.setHeight("100px");

        Button btnEnviar = new Button("Enviar Pedido al Parque", VaadinIcon.PAPERPLANE.create(), e -> {
            if (cmbItems.getValue() == null || txtMotivo.getValue().trim().isEmpty()) {
                Notification.show("Por favor, complete los campos obligatorios.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                solicitudService.crearSolicitud(empresaLogueada, cmbItems.getValue(), txtCantidad.getValue(), txtMotivo.getValue().trim());
                Notification.show("Solicitud enviada con éxito. El Administrador ha sido notificado.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                cmbItems.clear();
                txtMotivo.clear();
                txtCantidad.setValue(1);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> {
            cmbItems.clear();
            txtMotivo.clear();
            txtCantidad.setValue(1);
        });

        HorizontalLayout barraBotones = new HorizontalLayout(btnEnviar, btnCancelar);

        cardForm.add(filaFormulario, txtMotivo, barraBotones);

        VerticalLayout cardGrid = new VerticalLayout();
        cardGrid.setWidthFull();
        cardGrid.setPadding(true);
        cardGrid.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.MEDIUM, LumoUtility.Margin.Top.SMALL);
        cardGrid.getElement().getThemeList().add("shadow-sm");

        H3 subtituloDerecho = new H3("🛠️ Recursos en mi Poder");
        subtituloDerecho.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.NONE);
        Paragraph txtAyuda = new Paragraph("Avisá al Administrador cuando dejes de usar una herramienta para que vuelva a estar disponible.");
        txtAyuda.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);
        cardGrid.add(subtituloDerecho, txtAyuda);

        gridMisPrestamos.setWidthFull();
        gridMisPrestamos.setHeight("250px");

        gridMisPrestamos.addColumn(recurso -> recurso.getItem().getNombre()).setHeader("Recurso").setSortable(true);
        gridMisPrestamos.addColumn(recurso -> recurso.getItem().getCategoria()).setHeader("Categoría");
        gridMisPrestamos.addColumn(Recurso::getNumeroSerie).setHeader("Nro. Serie");
        gridMisPrestamos.addColumn(Recurso::getUbicacionFisica).setHeader("Ubicación Física");
        gridMisPrestamos.addComponentColumn(recurso -> {
            Button btnDevolver = new Button("Devolver", VaadinIcon.ARROW_BACKWARD.create(), e -> {
                try {
                    recurso.setEstadoMovimiento(EstadoMovimientoRecurso.A_DEVOLVER);
                    recursoService.guardarRecurso(recurso);
                    Notification.show("Aviso de devolución registrado.", 4000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    actualizarTablaPrestamos();
                } catch (Exception ex) {
                    Notification.show("Error al procesar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            btnDevolver.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            return btnDevolver;
        }).setHeader("Acción");

        cardGrid.add(gridMisPrestamos);

        layout.add(cardForm, cardGrid);
        return layout;
    }

    // PESTAÑA 2: OFRECER RECURSOS
    private VerticalLayout crearLayoutOfrecerHerramientas() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        layout.setSpacing(true);

        VerticalLayout cardForm = new VerticalLayout();
        cardForm.setWidthFull();
        cardForm.setPadding(true);
        cardForm.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.MEDIUM);
        cardForm.getElement().getThemeList().add("shadow-sm");

        H3 tituloForm = new H3("🤝 Aportar Material Propio al Parque");
        tituloForm.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL, LumoUtility.Border.BOTTOM, LumoUtility.Padding.Bottom.SMALL);
        cardForm.add(tituloForm);

        TextField txtNombreArticulo = new TextField("Nombre del Recurso / Herramienta *");
        txtNombreArticulo.setPlaceholder("Ej: Hormigonera, Amoladora");
        txtNombreArticulo.setWidthFull();

        TextField txtCategoriaArticulo = new TextField("Categoría *");
        txtCategoriaArticulo.setPlaceholder("Ej: Maquinaria Pesada, Insumo Eléctrico");
        txtCategoriaArticulo.setWidthFull();

        HorizontalLayout filaOfrecer1 = new HorizontalLayout(txtNombreArticulo, txtCategoriaArticulo);
        filaOfrecer1.setWidthFull();

        // Fila 2: Conservación, Ubicación y Nro Serie
        ComboBox<EstadoConservacionRecurso> cmbConservacion = new ComboBox<>("Estado Físico / Conservación *");
        cmbConservacion.setItems(EstadoConservacionRecurso.values());
        cmbConservacion.setWidthFull();

        TextField txtUbicacion = new TextField("Ubicación Física Recurso *");
        txtUbicacion.setPlaceholder("Ej: Sector Depósito");
        txtUbicacion.setWidthFull();

        TextField txtNroSerie = new TextField("Número de Serie (Opcional)");
        txtNroSerie.setPlaceholder("S/N si no posee");
        txtNroSerie.setWidthFull();

        HorizontalLayout filaOfrecer2 = new HorizontalLayout(cmbConservacion, txtUbicacion, txtNroSerie);
        filaOfrecer2.setWidthFull();

        Button btnGuardarAporte = new Button("Publicar y Poner a Disposición", VaadinIcon.CHECK.create(), e -> {
            if (txtNombreArticulo.getValue().trim().isEmpty() || txtCategoriaArticulo.getValue().trim().isEmpty()
                    || cmbConservacion.getValue() == null || txtUbicacion.getValue().trim().isEmpty()) {
                Notification.show("Por favor, complete los campos obligatorios.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                Item itemInventario = recursoService.obtenerTodoElInventario().stream()
                        .map(Recurso::getItem)
                        .filter(i -> i.getNombre().equalsIgnoreCase(txtNombreArticulo.getValue().trim()))
                        .findFirst()
                        .orElseGet(() -> {
                            Item nuevoItem = new Item();
                            nuevoItem.setNombre(txtNombreArticulo.getValue().trim());
                            nuevoItem.setCategoria(txtCategoriaArticulo.getValue().trim());
                            nuevoItem.setDescripcion("Aportado por la empresa " + empresaLogueada.getRazonSocial());
                            return nuevoItem;
                        });

                Recurso nuevoRecurso = new Recurso();
                nuevoRecurso.setItem(itemInventario);
                nuevoRecurso.setPropietario(empresaLogueada.getRazonSocial());
                nuevoRecurso.setPropietarioEmpresa(empresaLogueada);
                nuevoRecurso.setEstadoConservacion(cmbConservacion.getValue());
                nuevoRecurso.setUbicacionFisica(txtUbicacion.getValue().trim());
                nuevoRecurso.setNumeroSerie(txtNroSerie.getValue().trim().isEmpty() ? "S/N" : txtNroSerie.getValue().trim());
                nuevoRecurso.setEstadoMovimiento(EstadoMovimientoRecurso.DISPONIBLE);

                recursoService.guardarRecurso(nuevoRecurso);

                Notification.show("¡Material registrado con éxito!", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                txtNombreArticulo.clear();
                txtCategoriaArticulo.clear();
                cmbConservacion.clear();
                txtUbicacion.clear();
                txtNroSerie.clear();

                actualizarTablaAportes();
                refrescarComboItems();
            } catch (Exception ex) {
                Notification.show("Error al guardar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardarAporte.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cardForm.add(filaOfrecer1, filaOfrecer2, btnGuardarAporte);

        VerticalLayout cardGrid = new VerticalLayout();
        cardGrid.setWidthFull();
        cardGrid.setPadding(true);
        cardGrid.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.MEDIUM, LumoUtility.Margin.Top.SMALL);
        cardGrid.getElement().getThemeList().add("shadow-sm");

        H3 tituloGrid = new H3("📋 Mis Recursos Ofrecidos");
        tituloGrid.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.NONE);
        Paragraph txtAyudaGrid = new Paragraph("Gestione la disponibilidad de sus recursos. ¡Puede pausar cuando quiera los préstamos temporalmente!");
        txtAyudaGrid.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);
        cardGrid.add(tituloGrid, txtAyudaGrid);

        gridMisAportes.setWidthFull();
        gridMisAportes.setHeight("250px");

        gridMisAportes.addColumn(recurso -> recurso.getItem().getNombre()).setHeader("Material").setSortable(true);
        gridMisAportes.addColumn(recurso -> recurso.getItem().getCategoria()).setHeader("Categoría");
        gridMisAportes.addColumn(Recurso::getNumeroSerie).setHeader("Nro. Serie");
        gridMisAportes.addColumn(Recurso::getUbicacionFisica).setHeader("Ubicación");
        gridMisAportes.addColumn(Recurso::getEstadoMovimiento).setHeader("Estado Disponibilidad");

        gridMisAportes.addComponentColumn(recurso -> {
            boolean esDisponible = recurso.getEstadoMovimiento() == EstadoMovimientoRecurso.DISPONIBLE;

            Button btnAccion = new Button(esDisponible ? "Pausar Préstamo" : "Activar Préstamo");
            btnAccion.setIcon(esDisponible ? VaadinIcon.PAUSE.create() : VaadinIcon.PLAY.create());
            btnAccion.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

            if (recurso.getEstadoMovimiento() == EstadoMovimientoRecurso.PRESTADO) {
                btnAccion.setEnabled(false);
                btnAccion.setText("En Uso por Otro");
            }

            btnAccion.addClickListener(e -> {
                try {
                    if (recurso.getEstadoMovimiento() == EstadoMovimientoRecurso.DISPONIBLE) {
                        recurso.setEstadoMovimiento(EstadoMovimientoRecurso.PAUSADO);
                        Notification.show("Material retirado de la oferta temporalmente.");
                    } else {
                        recurso.setEstadoMovimiento(EstadoMovimientoRecurso.DISPONIBLE);
                        Notification.show("Material disponible nuevamente para préstamo.");
                    }
                    recursoService.guardarRecurso(recurso);
                    actualizarTablaAportes();
                    refrescarComboItems();
                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage());
                }
            });

            return btnAccion;
        }).setHeader("Acciones");

        cardGrid.add(gridMisAportes);

        layout.add(cardForm, cardGrid);
        return layout;
    }

    private void actualizarTablaPrestamos() {
        if (empresaLogueada != null) {
            gridMisPrestamos.setItems(recursoService.listarRecursosPrestadosA(empresaLogueada));
        }
    }

    private void actualizarTablaAportes() {
        if (empresaLogueada != null) {
            gridMisAportes.setItems(recursoService.obtenerTodoElInventario().stream()
                    .filter(r -> r.getPropietarioEmpresa() != null && r.getPropietarioEmpresa().getId().equals(empresaLogueada.getId()))
                    .collect(Collectors.toList()));
        }
    }

    private void refrescarComboItems() {
        if (cmbItems != null) {
            cmbItems.setItems(recursoService.obtenerTodoElInventario().stream()
                    .map(Recurso::getItem)
                    .distinct()
                    .toList());
        }
    }

    private boolean validarAccesoEmpresa() {
        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
        if (!(usuario instanceof RepresentanteEmpresa logueado)) return false;

        empresaLogueada = empresaService.obtenerEmpresaPorRepresentante(logueado);
        if (empresaLogueada == null || (empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.RADICADA && empresaLogueada.getEstadoEmpresa() != EstadoEmpresa.TITULADA)) {
            setSizeFull();
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);
            Span alerta = new Span("⚠️ Sección Exclusiva: Tu empresa debe estar Radicada o Titulada para operar con recursos.");
            alerta.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.FontWeight.BOLD);
            add(alerta);
            return false;
        }
        return true;
    }
}