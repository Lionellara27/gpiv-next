package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.model.InformeAvance;
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Detalle de Empresa | SGPIV")
@Route(value = "admin/empresa-detalle", layout = MainLayout.class)
public class EmpresaDetalleView extends VerticalLayout implements HasUrlParameter<Long> {

	private final EmpresaService empresaService;
	private Long currentEmpresaId;

	// Componentes de Cabecera
	private H2 nombreElement = new H2();
	private Span rubroElement = new Span();
	private Paragraph descElement = new Paragraph();
	private Span badgeCondicion = new Span();
	private Button btnTitular = new Button("Titular Empresa", VaadinIcon.DIPLOMA.create());
	private Button btnDesadjudicar = new Button("Desadjudicar Empresa", VaadinIcon.TRASH.create());
	private Button btnSolicitarInforme = new Button("Solicitar Informe de Avance", VaadinIcon.BELL.create()); // boton para la notificacion a la empresa

	// Grids de Datos Reales
	private Grid<Lote> gridLotes = new Grid<>(Lote.class, false);
	private Grid<Recurso> gridHerramientas = new Grid<>(Recurso.class, false);
	private VerticalLayout timelineAvances = new VerticalLayout();

	public EmpresaDetalleView(EmpresaService empresaService) {
		this.empresaService = empresaService;

		setPadding(true);
		setSpacing(true);
		getStyle().set("background-color", "#f5f7fa");

		Button btnVolver = new Button("Volver al listado", VaadinIcon.ARROW_LEFT.create());
		btnVolver.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(InformesEmpresasView.class)));
		add(btnVolver);

		// --- 1. CABECERA ---
		HorizontalLayout header = new HorizontalLayout();
		header.setWidthFull();
		header.setPadding(true);
		header.getStyle().set("background-color", "white").set("border-radius", "15px");
		header.setAlignItems(Alignment.CENTER);

		Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");
		logoEmpresa.setWidth("100px");
		logoEmpresa.setHeight("100px");
		logoEmpresa.getStyle().set("border-radius", "10px");

		VerticalLayout infoGral = new VerticalLayout();
		infoGral.setSpacing(false);
		infoGral.setPadding(false);

		nombreElement.addClassNames(LumoUtility.Margin.Vertical.NONE);
		rubroElement.getStyle().set("color", "#0063BE").set("font-weight", "bold");
		descElement.getStyle().set("color", "#666");

		// Configuración estética del Badge de condición
		badgeCondicion.getElement().getThemeList().add("badge");
		badgeCondicion.getStyle().set("margin-top", "5px");

		infoGral.add(nombreElement, rubroElement, descElement, badgeCondicion);

		// Configuración del botón de Escrituración / Titularidad
		btnTitular.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
		btnTitular.getStyle().set("margin-left", "auto"); // Lo empuja hacia el extremo derecho
		btnTitular.addClickListener(e -> abrirDialogoTitulacion());

		header.add(logoEmpresa, infoGral, btnTitular);
		// Configuración del botón de Desadjudicación
		btnDesadjudicar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
		btnDesadjudicar.getStyle().set("margin-left", "10px");
		btnDesadjudicar.addClickListener(e -> abrirDialogoDesadjudicacion());
		header.add(btnDesadjudicar);

		// --- LOGICA DE NOTIFICACION POR SESIÓN ---
		btnSolicitarInforme.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnSolicitarInforme.getStyle()
				.set("margin-left", "10px")
				.set("background-color", "#FF8C00")
				.set("color", "white")
				.set("font-weight", "bold");

		btnSolicitarInforme.addClickListener(e -> {
			java.util.List<Long> empresasNotificadas = (java.util.List<Long>) com.vaadin.flow.server.VaadinSession.getCurrent().getAttribute("alertasInformes");

			// Si es la primera vez, la lista va a ser null, entonces la creamos
			if (empresasNotificadas == null) {
				empresasNotificadas = new java.util.ArrayList<>();
			}

			// meto el ID de la empresa que el Admin está mirando en la lista (si no estaba ya)
			if (!empresasNotificadas.contains(currentEmpresaId)) {
				empresasNotificadas.add(currentEmpresaId);
			}

			// guardo la lista actualizada de nuevo en la sesion global
			com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("alertasInformes", empresasNotificadas);

			Notification.show("¡Notificación registrada en memoria para esta empresa!", 3000, Notification.Position.TOP_CENTER)
					.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		});

		header.add(btnSolicitarInforme);

		// --- 2. CUERPO: LOTES (IZQ) Y HERRAMIENTAS (DER) ---
		HorizontalLayout cuerpo = new HorizontalLayout();
		cuerpo.setWidthFull();
		cuerpo.setSpacing(true);

		// Panel Izquierdo: Lotes Asignados
		VerticalLayout sectionLotes = new VerticalLayout();
		sectionLotes.setWidth("50%");
		sectionLotes.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
		H3 tLotes = new H3("Lotes Asignados");
		configurarTablaLotes();
		sectionLotes.add(tLotes, gridLotes);

		// Panel Derecho: Inventario Integral (Aportado + Prestado)
		VerticalLayout sectionRecursos = new VerticalLayout();
		sectionRecursos.setWidth("50%");
		sectionRecursos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
		H3 tRecursos = new H3("Inventario de Herramientas y Recursos");
		configurarTablaHerramientas();
		sectionRecursos.add(tRecursos, gridHerramientas);

		cuerpo.add(sectionLotes, sectionRecursos);

		// --- 3. SECCIÓN DE HISTORIAL DE AVANCES ---
		VerticalLayout sectionAvances = new VerticalLayout();
		sectionAvances.setWidthFull();
		sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");

		H3 tAvance = new H3("Historial de Avances de Proyecto");
		timelineAvances.setSpacing(true);
		timelineAvances.setPadding(false);
		sectionAvances.add(tAvance, timelineAvances);

		add(header, cuerpo, sectionAvances);
	}

	private void configurarTablaLotes() {
		gridLotes.setWidthFull();
		gridLotes.setAllRowsVisible(true);
		gridLotes.addColumn(Lote::getManzana).setHeader("Manzana");
		gridLotes.addColumn(Lote::getNroLote).setHeader("Nro. Lote");
		gridLotes.addColumn(Lote::getUbicacion).setHeader("Ubicación");
		gridLotes.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
	}

	private void configurarTablaHerramientas() {
		gridHerramientas.setWidthFull();
		gridHerramientas.setAllRowsVisible(true);

		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getNombre() : "Recurso sin nombre")
				.setHeader("Nombre Item").setSortable(true);

		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getCategoria() : "General")
				.setHeader("Categoría");

		gridHerramientas.addColumn(Recurso::getNumeroSerie).setHeader("Nº Serie");

		gridHerramientas.addColumn(recurso -> {
			if (recurso.getPropietarioEmpresa() != null) {
				return "Aportado (Capital Propio)";
			} else {
				return "Prestado por el Parque";
			}
		}).setHeader("Origen / Condición").setSortable(true);
	}

	@Override
	public void setParameter(BeforeEvent event, Long idEmpresa) {
		if (idEmpresa == null) {
			event.rerouteTo(InformesEmpresasView.class);
			return;
		}

		this.currentEmpresaId = idEmpresa;
		refrescarDatos();
	}

	private void refrescarDatos() {
		Optional<Empresa> empresaOpt = empresaService.obtenerEmpresaCompletaPorId(currentEmpresaId);

		if (empresaOpt.isPresent()) {
			Empresa empresa = empresaOpt.get();

			// 1. Cargar datos de la Cabecera
			nombreElement.setText(empresa.getRazonSocial());
			descElement.setText("CUIT: " + empresa.getCuit() + " | Dirección Legal: " + empresa.getDireccion());

			if (empresa.getProyecto() != null) {
				rubroElement.setText("PROYECTO: " + empresa.getProyecto().getNombre() +
						" [" + empresa.getProyecto().getCategoria() + "]");
			} else {
				rubroElement.setText("Sin Proyecto Productivo Registrado");
			}

			// Renderizar el Badge Dinámico de EstadoEmpresa
			EstadoEmpresa est = empresa.getEstadoEmpresa() != null ? empresa.getEstadoEmpresa() : EstadoEmpresa.INTERESADA;
			badgeCondicion.setText("CONDICIÓN: " + est.toString());
			actualizarEstiloBadge(est);

			// Control de visibilidad del botón "Titular Empresa"
			// Solo se puede titular si ya está Radicada
			if (est == EstadoEmpresa.RADICADA) {
				btnTitular.setVisible(true);
				btnDesadjudicar.setVisible(true); // También permite desadjudicar si está RADICADA
				btnSolicitarInforme.setVisible(true);
			} else if (est == EstadoEmpresa.TITULADA) {
				btnTitular.setVisible(false);
				btnDesadjudicar.setVisible(true);
				btnSolicitarInforme.setVisible(true);
			} else {
				btnTitular.setVisible(false);
				btnDesadjudicar.setVisible(false);
			}

			// 2. Cargar tabla de Lotes
			gridLotes.setItems(empresa.getLotesAsignados());

			// 3. Combinar Listas de Herramientas
			List<Recurso> inventarioTotal = new ArrayList<>();
			if (empresa.getHerramientasAportadas() != null) {
				inventarioTotal.addAll(empresa.getHerramientasAportadas());
			}
			if (empresa.getHerramientasPrestadas() != null) {
				inventarioTotal.addAll(empresa.getHerramientasPrestadas());
			}
			gridHerramientas.setItems(inventarioTotal);

			// 4. Cargar Historial de Informes de Avance
			timelineAvances.removeAll();
			List<InformeAvance> informes = empresa.getInformesDeAvance();

			if (informes == null || informes.isEmpty()) {
				timelineAvances.add(new Paragraph("No se registran informes de avance cargados hasta la fecha."));
			} else {
				for (InformeAvance informe : informes) {
					timelineAvances.add(crearCardInformeAvance(informe));
				}
			}

		} else {
			Notification.show("La empresa seleccionada no existe.", 3000, Notification.Position.MIDDLE)
					.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
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

		Optional<Empresa> empresaOpt = empresaService.obtenerEmpresaCompletaPorId(currentEmpresaId);
		String nombreEmpresa = empresaOpt.isPresent() ? empresaOpt.get().getRazonSocial() : "la empresa";

		Paragraph advertencia = new Paragraph("⚠️ ADVERTENCIA: Esta acción es irreversible. " +
				"Se liberarán todos los lotes asignados a '" + nombreEmpresa +
				"' y su estado pasará a DESADJUDICADA.");
		advertencia.getStyle().set("color", "#d32f2f").set("font-weight", "bold").set("font-size", "0.95em");

		Paragraph instruccion = new Paragraph("Para completar el proceso, cargue el acta oficial de desadjudicación.");
		instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

		// Variables para almacenar el PDF
		final byte[][] pdfEnMemoria = {null};
		final String[] nombrePdf = {null};

		// Componente de carga
		com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
		upload.setAcceptedFileTypes("application/pdf");
		upload.setMaxFiles(1);

		com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer =
				new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
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
				Notification.show("Error al leer el archivo: " + ex.getMessage())
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		});

		upload.addFileRejectedListener(e -> {
			pdfEnMemoria[0] = null;
			nombrePdf[0] = null;
			btnConfirmarDesadjudicacion.setEnabled(false);
		});

		btnConfirmarDesadjudicacion.addClickListener(e -> {
			try {
				empresaService.desadjudicarEmpresa(currentEmpresaId, pdfEnMemoria[0], nombrePdf[0]);

				Notification.show("¡Empresa desadjudicada correctamente! Lotes liberados.",
								4000, Notification.Position.MIDDLE)
						.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

				dialog.close();
				refrescarDatos();
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

		Paragraph instruccion = new Paragraph("Para otorgar la titularidad definitiva a '" +
				empresaService.obtenerEmpresaCompletaPorId(currentEmpresaId).get().getRazonSocial() +
				"', cargue el acta de otorgamiento de escritura.");
		instruccion.getStyle().set("color", "#4A5568").set("font-size", "0.95em");

		// Variables para almacenar el PDF
		final byte[][] pdfEnMemoria = {null};
		final String[] nombrePdf = {null};

		// Componente de carga
		com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
		upload.setAcceptedFileTypes("application/pdf");
		upload.setMaxFiles(1);

		com.vaadin.flow.component.upload.receivers.MemoryBuffer buffer =
				new com.vaadin.flow.component.upload.receivers.MemoryBuffer();
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
				Notification.show("Error al leer el archivo: " + ex.getMessage())
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		});

		upload.addFileRejectedListener(e -> {
			pdfEnMemoria[0] = null;
			nombrePdf[0] = null;
			btnConfirmarTitulacion.setEnabled(false);
		});

		btnConfirmarTitulacion.addClickListener(e -> {
			try {
				Optional<Empresa> empresaOpt = empresaService.obtenerEmpresaCompletaPorId(currentEmpresaId);
				if (empresaOpt.isPresent()) {
					Empresa empresa = empresaOpt.get();

					// Guardar el PDF del acta en la empresa
					empresa.setPdfActaRadicacion(pdfEnMemoria[0]);
					empresa.setNombreActaRadicacion(nombrePdf[0]);

					// Cambiar estado a TITULADA
					empresaService.titularEmpresa(currentEmpresaId);

					Notification.show("¡Empresa titulada con éxito! Acta guardada.", 3000,
									Notification.Position.BOTTOM_END)
							.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
					dialog.close();
					refrescarDatos();
				}
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
//	private void abrirDialogoConfirmacion() {
//		Dialog dialog = new Dialog();
//		dialog.setHeaderTitle("Confirmar Titularidad (HU 11)");
//
//		VerticalLayout dialogLayout = new VerticalLayout(
//				new Paragraph("¿Está seguro de que desea otorgar el título de propiedad definitivo a esta empresa?"),
//				new Paragraph("Esta acción actualizará su estado a TITULADA y dejará constancia del otorgamiento de la escritura.")
//		);
//		dialogLayout.setPadding(false);
//		dialog.add(dialogLayout);
//
//		Button btnConfirmar = new Button("Otorgar Título", e -> {
//			try {
//				empresaService.titularEmpresa(currentEmpresaId);
//				Notification.show("¡Empresa titulada con éxito!", 3000, Notification.Position.BOTTOM_END)
//						.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//				dialog.close();
//				refrescarDatos(); // Recarga la pantalla de inmediato
//			} catch (Exception ex) {
//				Notification.show("Error al procesar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
//						.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			}
//		});
//		btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
//
//		Button btnCancelar = new Button("Cancelar", e -> dialog.close());
//		btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//		dialog.getFooter().add(btnCancelar, btnConfirmar);
//		dialog.open();
//	}

	private void actualizarEstiloBadge(EstadoEmpresa estado) {
		badgeCondicion.getStyle().remove("background-color");
		badgeCondicion.getStyle().remove("color");
		badgeCondicion.getElement().getThemeList().remove("success");

		switch (estado) {
			case INTERESADA:
				badgeCondicion.getStyle().set("background-color", "#e0f7fa").set("color", "#006064");
				break;
			case RADICADA:
				badgeCondicion.getElement().getThemeList().add("success");
				break;
			case TITULADA:
				badgeCondicion.getStyle().set("background-color", "#f3e5f5").set("color", "#4a148c");
				break;
		}
	}

	private VerticalLayout crearCardInformeAvance(InformeAvance informe) {
		VerticalLayout card = new VerticalLayout();
		card.setSpacing(false);
		card.getStyle()
				.set("border-left", "4px solid " + getColorPorEstado(informe.getEstadoCumplimiento()))
				.set("background-color", "#f9f9f9")
				.set("padding", "15px")
				.set("margin-bottom", "10px")
				.set("border-radius", "0 10px 10px 0");

		HorizontalLayout filaEncabezado = new HorizontalLayout();
		filaEncabezado.setWidthFull();
		filaEncabezado.setJustifyContentMode(JustifyContentMode.BETWEEN);

		Span txtFecha = new Span("Evaluado el: " +
				(informe.getFechaEvaluacion() != null ? informe.getFechaEvaluacion().toString() : "Pendiente"));
		txtFecha.getStyle().set("font-size", "0.85em").set("color", "#666");

		Span badgeEstado = new Span(informe.getEstadoCumplimiento() != null ? informe.getEstadoCumplimiento().toString() : "NULO");
		badgeEstado.getStyle()
				.set("background-color", getColorPorEstado(informe.getEstadoCumplimiento()))
				.set("color", "white")
				.set("padding", "2px 8px")
				.set("border-radius", "12px")
				.set("font-size", "0.75em")
				.set("font-weight", "bold");

		filaEncabezado.add(txtFecha, badgeEstado);

		H4 titulo = new H4(informe.getTitulo() != null ? informe.getTitulo() : "Informe sin título");
		Paragraph obs = new Paragraph(informe.getObservaciones());
		obs.getStyle().set("font-size", "0.9em").set("margin-top", "5px");

		if (informe.getArchivoPdf() != null) {
			Button btnDescargar = new Button("Descargar PDF: " + informe.getNombreArchivoPdf(), VaadinIcon.DOWNLOAD.create());
			btnDescargar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
			btnDescargar.addClickListener(e -> Notification.show("Iniciando descarga del informe..."));
			card.add(filaEncabezado, titulo, obs, btnDescargar);
		} else {
			card.add(filaEncabezado, titulo, obs);
		}

		return card;
	}

	private String getColorPorEstado(com.unrn.gpiv.common.EstadoCumplimiento estado) {
		if (estado == null) return "#999";
		switch (estado) {
			case TOTAL: return "#28a745";
			case PARCIAL: return "#ffc107";
			case INCUMPLIDO: return "#dc3545";
			case NULO: return "#6c757d";
			default: return "#0063BE";
		}
	}
}

//package com.unrn.gpiv.views.admin;
//
//import com.unrn.gpiv.model.Empresa;
//import com.unrn.gpiv.model.Lote;
//import com.unrn.gpiv.model.InformeAvance;
//import com.unrn.gpiv.model.Recurso;
//import com.unrn.gpiv.service.EmpresaService;
//import com.unrn.gpiv.views.MainLayout;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.BeforeEvent;
//import com.vaadin.flow.router.HasUrlParameter;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.theme.lumo.LumoUtility;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@PageTitle("Detalle de Empresa | SGPIV")
//@Route(value = "admin/empresa-detalle", layout = MainLayout.class)
//public class EmpresaDetalleView extends VerticalLayout implements HasUrlParameter<Long> {
//
//	private final EmpresaService empresaService;
//
//	// Componentes de Cabecera
//	private H2 nombreElement = new H2();
//	private Span rubroElement = new Span();
//	private Paragraph descElement = new Paragraph();
//
//	// Grids de Datos Reales
//	private Grid<Lote> gridLotes = new Grid<>(Lote.class, false);
//	private Grid<Recurso> gridHerramientas = new Grid<>(Recurso.class, false);
//	private VerticalLayout timelineAvances = new VerticalLayout();
//
//	public EmpresaDetalleView(EmpresaService empresaService) {
//		this.empresaService = empresaService;
//
//		setPadding(true);
//		setSpacing(true);
//		getStyle().set("background-color", "#f5f7fa");
//
//		Button btnVolver = new Button("Volver al listado", VaadinIcon.ARROW_LEFT.create());
//		btnVolver.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(InformesEmpresasView.class)));
//		add(btnVolver);
//
//		// --- 1. CABECERA ---
//		HorizontalLayout header = new HorizontalLayout();
//		header.setWidthFull();
//		header.setPadding(true);
//		header.getStyle().set("background-color", "white").set("border-radius", "15px");
//		header.setAlignItems(Alignment.CENTER);
//
//		Image logoEmpresa = new Image("https://via.placeholder.com/100", "Logo Empresa");
//		logoEmpresa.setWidth("100px");
//		logoEmpresa.setHeight("100px");
//		logoEmpresa.getStyle().set("border-radius", "10px");
//
//		VerticalLayout infoGral = new VerticalLayout();
//		infoGral.setSpacing(false);
//		infoGral.setPadding(false);
//
//		nombreElement.addClassNames(LumoUtility.Margin.Vertical.NONE);
//		rubroElement.getStyle().set("color", "#0063BE").set("font-weight", "bold");
//		descElement.getStyle().set("color", "#666");
//
//		infoGral.add(nombreElement, rubroElement, descElement);
//		header.add(logoEmpresa, infoGral);
//
//		// --- 2. CUERPO: LOTES (IZQ) Y HERRAMIENTAS (DER) ---
//		HorizontalLayout cuerpo = new HorizontalLayout();
//		cuerpo.setWidthFull();
//		cuerpo.setSpacing(true);
//
//		// Panel Izquierdo: Lotes Asignados
//		VerticalLayout sectionLotes = new VerticalLayout();
//		sectionLotes.setWidth("50%");
//		sectionLotes.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
//		H3 tLotes = new H3("Lotes Asignados");
//		configurarTablaLotes();
//		sectionLotes.add(tLotes, gridLotes);
//
//		// Panel Derecho: Inventario Integral (Aportado + Prestado)
//		VerticalLayout sectionRecursos = new VerticalLayout();
//		sectionRecursos.setWidth("50%");
//		sectionRecursos.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
//		H3 tRecursos = new H3("Inventario de Herramientas y Recursos");
//		configurarTablaHerramientas();
//		sectionRecursos.add(tRecursos, gridHerramientas);
//
//		cuerpo.add(sectionLotes, sectionRecursos);
//
//		// --- 3. SECCIÓN DE HISTORIAL DE AVANCES ---
//		VerticalLayout sectionAvances = new VerticalLayout();
//		sectionAvances.setWidthFull();
//		sectionAvances.getStyle().set("background-color", "white").set("border-radius", "15px").set("padding", "1.5em");
//
//		H3 tAvance = new H3("Historial de Avances de Proyecto");
//		timelineAvances.setSpacing(true);
//		timelineAvances.setPadding(false);
//		sectionAvances.add(tAvance, timelineAvances);
//
//		add(header, cuerpo, sectionAvances);
//	}
//
//	private void configurarTablaLotes() {
//		gridLotes.setWidthFull();
//		gridLotes.setAllRowsVisible(true);
//		gridLotes.addColumn(Lote::getManzana).setHeader("Manzana");
//		gridLotes.addColumn(Lote::getNroLote).setHeader("Nro. Lote");
//		gridLotes.addColumn(Lote::getUbicacion).setHeader("Ubicación");
//		gridLotes.addColumn(lote -> lote.getSuperficie() + " m²").setHeader("Superficie");
//	}
//
//	private void configurarTablaHerramientas() {
//		gridHerramientas.setWidthFull();
//		gridHerramientas.setAllRowsVisible(true);
//
//		// Mapeo seguro navegando desde Recurso hacia el Item relacionado
//		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getNombre() : "Recurso sin nombre")
//				.setHeader("Nombre Item").setSortable(true);
//
//		gridHerramientas.addColumn(recurso -> recurso.getItem() != null ? recurso.getItem().getCategoria() : "General")
//				.setHeader("Categoría");
//
//		gridHerramientas.addColumn(Recurso::getNumeroSerie).setHeader("Nº Serie");
//
//		// Columna para calcular dinámicamente si es capital propio o préstamo del Parque Industrial
//		gridHerramientas.addColumn(recurso -> {
//			if (recurso.getPropietarioEmpresa() != null) {
//				return "Aportado (Capital Propio)";
//			} else {
//				return "Prestado por el Parque";
//			}
//		}).setHeader("Origen / Condición").setSortable(true);
//	}
//
//	@Override
//	public void setParameter(BeforeEvent event, Long idEmpresa) {
//		if (idEmpresa == null) {
//			event.rerouteTo(InformesEmpresasView.class);
//			return;
//		}
//
//		// Usamos el nuevo método del servicio que inicializa las colecciones @OneToMany
//		Optional<Empresa> empresaOpt = empresaService.obtenerEmpresaCompletaPorId(idEmpresa);
//
//		if (empresaOpt.isPresent()) {
//			Empresa empresa = empresaOpt.get();
//
//			// 1. Cargar datos de la Cabecera
//			nombreElement.setText(empresa.getRazonSocial());
//			descElement.setText("CUIT: " + empresa.getCuit() + " | Dirección Legal: " + empresa.getDireccion());
//
//			if (empresa.getProyecto() != null) {
//				rubroElement.setText("PROYECTO: " + empresa.getProyecto().getNombre() +
//						" [" + empresa.getProyecto().getCategoria() + "]");
//			} else {
//				rubroElement.setText("Sin Proyecto Productivo Registrado");
//			}
//
//			// 2. Cargar tabla de Lotes
//			gridLotes.setItems(empresa.getLotesAsignados());
//
//			// 3. Combinar Listas de Herramientas (Aportadas + Prestadas)
//			List<Recurso> inventarioTotal = new ArrayList<>();
//			if (empresa.getHerramientasAportadas() != null) {
//				inventarioTotal.addAll(empresa.getHerramientasAportadas());
//			}
//			if (empresa.getHerramientasPrestadas() != null) {
//				inventarioTotal.addAll(empresa.getHerramientasPrestadas());
//			}
//			gridHerramientas.setItems(inventarioTotal);
//
//			// 4. Cargar Historial de Informes de Avance de forma dinámica
//			timelineAvances.removeAll();
//			List<InformeAvance> informes = empresa.getInformesDeAvance();
//
//			if (informes == null || informes.isEmpty()) {
//				timelineAvances.add(new Paragraph("No se registran informes de avance cargados hasta la fecha."));
//			} else {
//				for (InformeAvance informe : informes) {
//					timelineAvances.add(crearCardInformeAvance(informe));
//				}
//			}
//
//		} else {
//			Notification.show("La empresa seleccionada no existe.", 3000, Notification.Position.MIDDLE)
//					.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			event.rerouteTo(InformesEmpresasView.class);
//		}
//	}
//
//	private VerticalLayout crearCardInformeAvance(InformeAvance informe) {
//		VerticalLayout card = new VerticalLayout();
//		card.setSpacing(false);
//		card.getStyle()
//				.set("border-left", "4px solid " + getColorPorEstado(informe.getEstadoCumplimiento()))
//				.set("background-color", "#f9f9f9")
//				.set("padding", "15px")
//				.set("margin-bottom", "10px")
//				.set("border-radius", "0 10px 10px 0");
//
//		// Fila superior: Fecha y Badge de Estado
//		HorizontalLayout filaEncabezado = new HorizontalLayout();
//		filaEncabezado.setWidthFull();
//		filaEncabezado.setJustifyContentMode(JustifyContentMode.BETWEEN);
//
//		Span txtFecha = new Span("Evaluado el: " +
//				(informe.getFechaEvaluacion() != null ? informe.getFechaEvaluacion().toString() : "Pendiente"));
//		txtFecha.getStyle().set("font-size", "0.85em").set("color", "#666");
//
//		Span badgeEstado = new Span(informe.getEstadoCumplimiento().toString());
//		badgeEstado.getStyle()
//				.set("background-color", getColorPorEstado(informe.getEstadoCumplimiento()))
//				.set("color", "white")
//				.set("padding", "2px 8px")
//				.set("border-radius", "12px")
//				.set("font-size", "0.75em")
//				.set("font-weight", "bold");
//
//		filaEncabezado.add(txtFecha, badgeEstado);
//
//		// Contenido
//		H4 titulo = new H4(informe.getTitulo() != null ? informe.getTitulo() : "Informe sin título");
//		Paragraph obs = new Paragraph(informe.getObservaciones());
//		obs.getStyle().set("font-size", "0.9em").set("margin-top", "5px");
//
//		// Botón de descarga de PDF (si existe)
//		if (informe.getArchivoPdf() != null) {
//			Button btnDescargar = new Button("Descargar PDF: " + informe.getNombreArchivoPdf(), VaadinIcon.DOWNLOAD.create());
//			btnDescargar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
//
//			// Aquí podrías usar un StreamResource para la descarga
//			btnDescargar.addClickListener(e -> {
//				Notification.show("Iniciando descarga del informe...");
//				// Lógica de descarga aquí
//			});
//			card.add(filaEncabezado, titulo, obs, btnDescargar);
//		} else {
//			card.add(filaEncabezado, titulo, obs);
//		}
//
//		return card;
//	}
//
//	private String getColorPorEstado(com.unrn.gpiv.common.EstadoCumplimiento estado) {
//		if (estado == null) return "#999";
//		switch (estado) {
//			case TOTAL: return "#28a745";    // Verde
//			case PARCIAL: return "#ffc107";  // Amarillo/Naranja
//			case INCUMPLIDO: return "#dc3545"; // Rojo
//			case NULO: return "#6c757d";     // Gris
//			default: return "#0063BE";
//		}
//	}
//}