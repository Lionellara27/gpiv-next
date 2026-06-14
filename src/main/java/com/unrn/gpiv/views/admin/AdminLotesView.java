package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.HistorialService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;

@PageTitle("Gestión de Lotes | SGPIV")
@Route(value = "admin/lotes", layout = MainLayout.class)
public class AdminLotesView extends VerticalLayout implements BeforeEnterObserver {

    private final HistorialService historialService;
    private final LoteService loteService;
    private final EmpresaService empresaService;
    private Grid<Lote> grid = new Grid<>(Lote.class, false);
    private Binder<Lote> binder = new BeanValidationBinder<>(Lote.class);

    private TextField manzana = new TextField("Manzana");
    private TextField nroLote = new TextField("Nro. Lote");
    private NumberField superficie = new NumberField("Superficie (m²)");
    private ComboBox<EstadoLote> estado = new ComboBox<>("Estado", EstadoLote.values());
    private TextField ubicacion = new TextField("Ubicación");
    private TextArea caracteristicas = new TextArea("Características");
    private ComboBox<Empresa> empresaAsignada = new ComboBox<>("Asignar Empresa/Productor");
    private ComboBox<EstadoLote> filtroEstado = new ComboBox<>("Filtrar por Estado", EstadoLote.values());

    private Button guardar = new Button("Guardar");
    private Button cancelar = new Button("Cancelar");
    private Button editar = new Button("Editar Lote");
    private Button eliminar = new Button("Eliminar Lote");
    private Button desasignar = new Button("Desasignar Empresa");

    private VerticalLayout panelEdicion;

    public AdminLotesView(LoteService loteService, EmpresaService empresaService, HistorialService historialService) {
        this.loteService = loteService;
        this.empresaService = empresaService;
        this.historialService = historialService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Gestión de Lotes");
        Button btnAgregar = new Button("Agregar Lote", e ->
                getUI().ifPresent(ui -> ui.navigate(RegistrarLotesView.class))
        );
        btnAgregar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        filtroEstado.setPlaceholder("Todos los estados");
        filtroEstado.setClearButtonVisible(true);
        filtroEstado.addValueChangeListener(e -> filtrarLotes());

        HorizontalLayout barraHerramientas = new HorizontalLayout(filtroEstado, btnAgregar);
        barraHerramientas.setAlignItems(Alignment.BASELINE);
        barraHerramientas.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(titulo, barraHerramientas);
        header.setWidthFull();
        header.setFlexGrow(1, titulo);
        header.setVerticalComponentAlignment(Alignment.CENTER, barraHerramientas);

        tablaPrincipalLotes();
        this.panelEdicion = formularioDeEdicionLote();

        HorizontalLayout content = new HorizontalLayout(grid, panelEdicion);
        content.setSizeFull();
        content.setFlexGrow(1, grid);
        content.setFlexGrow(0, panelEdicion);

        add(header, content);
        actualizarTabla();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        actualizarTabla();
    }

    private VerticalLayout formularioDeEdicionLote() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(manzana, nroLote, ubicacion, superficie, estado, caracteristicas, empresaAsignada);
        formLayout.setColspan(caracteristicas, 2);

        empresaAsignada.setItemLabelGenerator(Empresa::getRazonSocial);
        formLayout.setWidthFull();
        binder.bindInstanceFields(this);

        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardar.addClickListener(event -> accionGuardar());
        cancelar.addClickListener(event -> limpiarFormulario());

        editar.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        editar.addClickListener(event -> activarModoEdicion(true));

        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        eliminar.addClickListener(event -> accionEliminar());

        desasignar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY); // Estilo rojo pero sutil
        desasignar.addClickListener(event -> accionDesadjudicar());

        HorizontalLayout accionesPrincipales = new HorizontalLayout(editar,desasignar, eliminar);
        HorizontalLayout barraGuardado = new HorizontalLayout(guardar, cancelar);

        VerticalLayout panel = new VerticalLayout(new H2("Detalles del Lote"), formLayout, accionesPrincipales, barraGuardado);
        panel.setWidth("400px");
        panel.setMinWidth("350px");
        panel.setVisible(false);
        panel.setId("formulario-edicion");

        panel.getStyle().set("border-left", "1px solid #e5e5e5");
        panel.getStyle().set("background-color", "#fcfcfc");

        return panel;
    }

    private void activarModoEdicion(boolean editable) {
        manzana.setReadOnly(!editable);
        nroLote.setReadOnly(!editable);
        ubicacion.setReadOnly(!editable);
        superficie.setReadOnly(!editable);
        estado.setReadOnly(!editable);
        caracteristicas.setReadOnly(!editable);

        guardar.setVisible(editable || empresaAsignada.isEnabled());
        cancelar.setVisible(editable || empresaAsignada.isEnabled());
        editar.setVisible(!editable);

        if (editable) { //oculto el desasignar si entra en modo edicion
            desasignar.setVisible(false);
        }
    }
    private void accionGuardar() {
        try {
            Lote lote = grid.asSingleSelect().getValue();
            if (lote == null) return;

            if (binder.writeBeanIfValid(lote)) {
                if (empresaAsignada.getValue() != null) {
                    Empresa empresaAAsignar = empresaAsignada.getValue();

                    // Seteamos la relación en el lote
                    lote.setEmpresa(empresaAAsignar);

                    if (lote.getEstado() == EstadoLote.LIBRE) {
                        lote.setEstado(EstadoLote.OCUPADO);

                        // Guarda en historial
                        historialService.registrarAsignacion(
                                empresaAAsignar.getRazonSocial(),
                                empresaAAsignar.getCuit(),
                                lote.getManzana(),
                                lote.getNroLote()
                        );
                    }

                    // Aseguramos que la empresa tenga el lote en su lista local antes de evaluar
                    if (!empresaAAsignar.getLotesAsignados().contains(lote)) {
                        empresaAAsignar.getLotesAsignados().add(lote);
                    }

                    // TRANSICIÓN DE ESTADO DE LA EMPRESA:
                    if (empresaAAsignar.getEstadoEmpresa() == com.unrn.gpiv.common.EstadoEmpresa.INTERESADA) {
                        empresaAAsignar.setEstadoEmpresa(com.unrn.gpiv.common.EstadoEmpresa.RADICADA);
                    }

                    empresaService.actualizarEmpresa(empresaAAsignar);

                } else {
                    // Si se seleccionó "Ninguna", removemos el lote de la empresa que lo tenía antes
                    if (lote.getEmpresa() != null) {
                        Empresa empresaAnterior = lote.getEmpresa();
                        Optional<Empresa> empCompletaOpt = empresaService.obtenerEmpresaCompletaPorId(empresaAnterior.getId());
                        if (empCompletaOpt.isPresent()) {
                            Empresa empCompleta = empCompletaOpt.get();
                            empCompleta.getLotesAsignados().removeIf(l -> l.getId().equals(lote.getId()));

                            if (empCompleta.getLotesAsignados().isEmpty() && empCompleta.getEstadoEmpresa() == EstadoEmpresa.RADICADA) {
                                empCompleta.setEstadoEmpresa(EstadoEmpresa.INTERESADA);
                            }
                            empresaService.actualizarEmpresa(empCompleta);
                        }
                    }
                    lote.setEmpresa(null);
                    lote.setEstado(EstadoLote.LIBRE);
                }

                // Guardamos el lote
                loteService.guardar(lote);
                Notification.show("Cambios aplicados correctamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                actualizarTabla();
                limpiarFormulario();
            } else {
                Notification.show("Por favor, revise los campos marcados en rojo", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
//    private void accionGuardar() {
//        try {
//            Lote lote = grid.asSingleSelect().getValue();
//            if (lote == null) return;
//
//            if (binder.writeBeanIfValid(lote)) {
//                if (empresaAsignada.getValue() != null) {
//                    lote.setEmpresa(empresaAsignada.getValue());
//                    if (lote.getEstado() == EstadoLote.LIBRE) {
//                        lote.setEstado(EstadoLote.OCUPADO);
//
//                        //guarda en historial
//                        historialService.registrarAsignacion(
//                                lote.getEmpresa().getRazonSocial(),
//                                lote.getEmpresa().getCuit(),
//                                lote.getManzana(),
//                                lote.getNroLote()
//                        );
//                    }
//                } else {
//                    lote.setEmpresa(null);
//                    lote.setEstado(EstadoLote.LIBRE);
//                }
//
//                loteService.guardar(lote);
//                Notification.show("Cambios aplicados correctamente", 3000, Notification.Position.TOP_CENTER)
//                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//
//                actualizarTabla();
//                limpiarFormulario();
//            } else {
//                Notification.show("Por favor, revise los campos marcados en rojo", 3000, Notification.Position.TOP_CENTER)
//                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            }
//        } catch (Exception e) {
//            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//        }
//    }
    private void accionDesadjudicar() {
        Lote lote = grid.asSingleSelect().getValue();
        if (lote == null) return;

        if (lote.getEmpresa() == null) {
            Notification.show("Este lote no tiene ninguna empresa asignada.");
            return;
        }

        try {
            Empresa empresaAfectada = lote.getEmpresa();

            // Antes de desasignar guarda en historial
            historialService.registrarDesasignacion(lote.getManzana(), lote.getNroLote());

            // Rompemos la relación en el objeto Lote
            lote.setEmpresa(null);
            lote.setEstado(EstadoLote.LIBRE);
            lote.setFechaAsignacion(null);

            loteService.guardar(lote);

            Optional<Empresa> empCompletaOpt = empresaService.obtenerEmpresaCompletaPorId(empresaAfectada.getId());
            if (empCompletaOpt.isPresent()) {
                Empresa empCompleta = empCompletaOpt.get();

                empCompleta.getLotesAsignados().removeIf(l -> l.getId().equals(lote.getId()));

                // Si verdaderamente ya no le quedan otros lotes y figuraba como RADICADA, vuelve a ser INTERESADA
                if (empCompleta.getLotesAsignados().isEmpty() && empCompleta.getEstadoEmpresa() == EstadoEmpresa.RADICADA) {
                    empCompleta.setEstadoEmpresa(EstadoEmpresa.INTERESADA);
                }
                empresaService.actualizarEmpresa(empCompleta);
            }

            Notification.show("Empresa desadjudicada con éxito", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            actualizarTabla();
            limpiarFormulario();
        } catch (Exception e) {
            Notification.show("Error al desadjudicar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
//    private void accionDesasignar() {
//        Lote lote = grid.asSingleSelect().getValue();
//        if (lote == null) return;
//
//        // Doble seguridad por las dudas
//        if (lote.getEmpresa() == null) {
//            Notification.show("Este lote no tiene ninguna empresa asignada.");
//            return;
//        }
//
//        try {
//            //antes de desasignar guarda en historial
//            historialService.registrarDesasignacion(lote.getManzana(), lote.getNroLote());
//            // Hacemos la desasignación física en el objeto
//            lote.setEmpresa(null);
//            lote.setEstado(EstadoLote.LIBRE);
//            lote.setFechaAsignacion(null);
//            // Si tenés setFechaAsignacion(null), agregalo acá también
//
//            // Guardamos el cambio en la base de datos
//            loteService.guardar(lote);
//
//            Notification.show("Empresa desasignada con éxito", 3000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//
//            actualizarTabla();
//            limpiarFormulario();
//        } catch (Exception e) {
//            Notification.show("Error al desasignar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//        }
//    }

    private void accionEliminar() {
        Lote lote = grid.asSingleSelect().getValue();
        if (lote == null) return;

        if (lote.getEmpresa() != null) {
            Notification.show("No se puede eliminar un lote que tiene una empresa asignada", 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            loteService.eliminar(lote);
            Notification.show("Lote eliminado con éxito", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            actualizarTabla();
            limpiarFormulario();
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editarLote(Lote lote) {
        if (lote == null) {
            limpiarFormulario();
        } else {
            binder.setBean(lote);
            empresaAsignada.setItems(empresaService.listarTodasLasAprobadas());

            // Bloqueamos los atributos estructurales por defecto al seleccionar de la tabla
            activarModoEdicion(false);

            switch (lote.getEstado()) {
                case LIBRE:
                    empresaAsignada.setValue(null);
                    empresaAsignada.setEnabled(true);
                    empresaAsignada.setHelperText("Seleccione una empresa");
                    break;
                case RESERVADO:
                case OCUPADO:
                case OCIOSO:
                    empresaAsignada.setValue(lote.getEmpresa());
                    empresaAsignada.setEnabled(lote.getEstado() == EstadoLote.RESERVADO);
                    empresaAsignada.setHelperText(lote.getEstado() == EstadoLote.OCUPADO ? "Lote activo" : "Lote bajo alerta o reserva");
                    break;
            }

            // Si el selector de empresa está habilitado, mostramos guardar/cancelar para confirmar asignación
            guardar.setVisible(empresaAsignada.isEnabled());
            cancelar.setVisible(true);
            desasignar.setVisible(lote.getEmpresa() != null);
            panelEdicion.setVisible(true);


        }
    }

    private void limpiarFormulario() {
        binder.setBean(null);
        empresaAsignada.setValue(null);
        grid.asSingleSelect().clear();
        panelEdicion.setVisible(false);
    }

    private void actualizarTabla() {
        if (filtroEstado != null && filtroEstado.getValue() != null) {
            filtrarLotes();
        } else {
            grid.setItems(loteService.listarTodos());
        }
    }

    private void filtrarLotes() {
        EstadoLote estadoSeleccionado = filtroEstado.getValue();
        if (estadoSeleccionado == null) {
            grid.setItems(loteService.listarTodos());
        } else {
            // Se asume que la implementación de filtrado correspondiente existe en loteService
            grid.setItems(loteService.listarTodos().stream().filter(l -> l.getEstado() == estadoSeleccionado).toList());
        }
    }

    private void tablaPrincipalLotes() {
        grid.setSizeFull();
        grid.addColumn(Lote::getManzana).setHeader("Manzana").setSortable(true);
        grid.addColumn(Lote::getNroLote).setHeader("Nro. Lote").setSortable(true);
        grid.addColumn(Lote::getUbicacion).setHeader("Ubicación");
        grid.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
        grid.addColumn(lote -> lote.getEmpresa() != null ? lote.getEmpresa().getRazonSocial() : "Sin Asignar")
                .setHeader("Empresa Asignada").setSortable(true);
        grid.addColumn(Lote::getCaracteristicas).setHeader("Características");

        grid.addComponentColumn(lote -> {
            com.vaadin.flow.component.html.Span badge = new com.vaadin.flow.component.html.Span(lote.getEstado().toString());
            badge.getElement().getThemeList().add("badge");
            switch (lote.getEstado()) {
                case LIBRE: badge.getElement().getThemeList().add("badge success"); break;
                case RESERVADO: badge.getStyle().set("background-color", "#fff3e0").set("color", "#b78103"); break;
                case OCUPADO: badge.getStyle().set("background-color", "#e0f7fa").set("color", "#006064"); break;
                case OCIOSO: badge.getElement().getThemeList().add("badge error"); break;
            }
            return badge;
        }).setHeader("Estado").setSortable(true);

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editarLote(event.getValue()));
    }
}