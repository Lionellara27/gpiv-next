package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.model.Lote;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Registrar Lote | SGPIV")
@Route(value = "admin/lote/registrar-lote", layout = MainLayout.class)
public class RegistrarLotesView extends VerticalLayout {

	private final LoteService loteService;
	private BeanValidationBinder<Lote> binder = new BeanValidationBinder<>(Lote.class);

	private TextField manzana = new TextField("Manzana");
	private TextField nroLote = new TextField("Número de Lote");
	private TextField ubicacion = new TextField("Ubicacion");
	private NumberField superficie = new NumberField("Superficie (m²)");
	private TextArea caracteristicas = new TextArea("Caracteristicas");

	public RegistrarLotesView(LoteService loteService) {
		this.loteService = loteService;
		add(new H2("Registrar Nuevo Lote"));

		FormLayout form = new FormLayout(manzana, nroLote, ubicacion, superficie, caracteristicas);
		binder.bindInstanceFields(this);

		Button guardar = new Button("Registrar Lote", e -> guardarNuevoLote());
		guardar.addThemeNames("primary");
		Button cancelar = new Button("Cancelar", e -> getUI().ifPresent(ui -> ui.navigate("admin/lotes")));
		add(form, new HorizontalLayout(guardar, cancelar));
	}

	private void guardarNuevoLote() {
		Lote nuevoLote = new Lote();

		if (binder.writeBeanIfValid(nuevoLote)) {
			try {
				loteService.guardar(nuevoLote);
				Notification.show("Lote registrado con exito", 3000, Notification.Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

				getUI().ifPresent(ui -> ui.navigate("admin/lotes"));
			} catch (Exception ex) {
				Notification.show("Error al guardar: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
						.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		} else {
			Notification.show("Por favor, revise los errores en el formulario",
					3000, Notification.Position.TOP_CENTER);
		}
	}
}