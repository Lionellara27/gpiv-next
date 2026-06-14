package com.unrn.gpiv.views.empresa;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Mi Empresa | SGPIV")
@Route(value = "mi-empresa", layout = MainLayout.class)
public class MiEmpresaView extends VerticalLayout {

    private final EmpresaService empresaService;

    public MiEmpresaView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "900px").set("margin", "0 auto");

        //RECUPERAR USUARIO
        Usuario usuarioLogueado = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");
        if (!(usuarioLogueado instanceof RepresentanteEmpresa logueado)) {
            add(new H2("Acceso denegado."));
            return;
        }

        //RECUPERAR DATOS EMPRESA
        Empresa empresaData = empresaService.obtenerEmpresaPorRepresentante(logueado);

        // --- CONTROL DE ALERTAS DE INFORMES ---
        java.util.List<Long> alertasActivas = (java.util.List<Long>) VaadinSession.getCurrent().getAttribute("alertasInformes");

        if (empresaData != null && alertasActivas != null && alertasActivas.contains(empresaData.getId())) {
            HorizontalLayout bannerAlerta = new HorizontalLayout();
            bannerAlerta.setWidthFull();
            bannerAlerta.setAlignItems(Alignment.CENTER);
            bannerAlerta.setJustifyContentMode(JustifyContentMode.BETWEEN);

            bannerAlerta.getStyle()
                    .set("background-color", "#FFF9E6")
                    .set("color", "#8A6D3B")
                    .set("padding", "16px 20px")
                    .set("border-radius", "12px")
                    .set("border", "1px solid #FCEFA1")
                    .set("margin-bottom", "20px");

            Span mensaje = new Span("⚠️ ATENCIÓN: El Administrador del Parque solicita que cargues los Informes de Avance faltantes a la brevedad.");
            mensaje.getStyle().set("font-weight", "bold");

            Button btnEntendido = new Button("Entendido", e -> {
                bannerAlerta.setVisible(false); // Oculta el cartel visualmente

                // Sacamos la ID de esta empresa de la lista global de la memoria para que no vuelva a saltar
                alertasActivas.remove(empresaData.getId());
                VaadinSession.getCurrent().setAttribute("alertasInformes", alertasActivas);
            });
            btnEntendido.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

            bannerAlerta.add(mensaje, btnEntendido);

            addComponentAsFirst(bannerAlerta);
        }

        //CONTROL ESTRICTO:
        //Si la empresa ya tiene dirección, o ya tiene tipo de sociedad, ya se da por registrada
        boolean yaRegistrada = (empresaData != null &&
                empresaData.getDireccion() != null && !empresaData.getDireccion().trim().isEmpty() &&
                empresaData.getTipoSociedad() != null && !empresaData.getTipoSociedad().trim().isEmpty());

        H2 titulo = new H2("Registro Final de la Empresa");
        add(titulo);

        // DATOS PRE-CARGADOS ---------------------------------------------
        VerticalLayout datosExistentesCard = new VerticalLayout();
        datosExistentesCard.getStyle().set("background-color", "#F8FAFC").set("border", "1px solid #E2E8F0").set("border-radius", "12px");

        TextField txtRazonSocial = new TextField("Razón Social");
        txtRazonSocial.setValue(logueado.getNombreCompleto());
        txtRazonSocial.setReadOnly(true);

        TextField txtCuit = new TextField("CUIT");
        txtCuit.setValue(logueado.getCuitPersonal() != null ? logueado.getCuitPersonal() : "No registrado");
        txtCuit.setReadOnly(true);

        TextField txtTelefono = new TextField("Teléfono Personal");
        txtTelefono.setValue(logueado.getTelefono() != null ? logueado.getTelefono() : "No registrado");
        txtTelefono.setReadOnly(true);

        datosExistentesCard.add(new H3("🏢 Datos Iniciales"), new FormLayout(txtRazonSocial, txtCuit, txtTelefono));

        // FORMULARIO-----------------------------------------------
        FormLayout formNuevosDatos = new FormLayout();
        TextField txtDomicilio = new TextField("Domicilio Legal");
        ComboBox<String> comboSociedad = new ComboBox<>("Tipo Sociedad", "S.A.", "S.R.L.", "S.A.S.", "Monotributista", "Cooperativa");
        TextField txtTelEmergencia = new TextField("Teléfono Emergencia");
        TextField txtInscripcion = new TextField("N° Inscripción Registral");

        // Lógica de Registro/Lectura (Precarga si ya completó el flujo alguna vez)
        if (yaRegistrada) {
            txtDomicilio.setValue(empresaData.getDireccion() != null ? empresaData.getDireccion() : "");
            comboSociedad.setValue(empresaData.getTipoSociedad() != null ? empresaData.getTipoSociedad() : "");
            txtTelEmergencia.setValue(empresaData.getTelefonoEmergencia() != null ? empresaData.getTelefonoEmergencia() : "");
            txtInscripcion.setValue(empresaData.getInscripcionRegistral() != null ? empresaData.getInscripcionRegistral() : "");

            txtDomicilio.setReadOnly(true);
            comboSociedad.setReadOnly(true);
            txtTelEmergencia.setReadOnly(true);
            txtInscripcion.setReadOnly(true);
        } else if (empresaData != null) {
            //Por si se se le asignó un lote a la empresa que todavia no lleno los datos, se dejan habilitados los campos
            txtDomicilio.setValue(empresaData.getDireccion() != null ? empresaData.getDireccion() : "");
            comboSociedad.setValue(empresaData.getTipoSociedad() != null ? empresaData.getTipoSociedad() : "");
            txtTelEmergencia.setValue(empresaData.getTelefonoEmergencia() != null ? empresaData.getTelefonoEmergencia() : "");
            txtInscripcion.setValue(empresaData.getInscripcionRegistral() != null ? empresaData.getInscripcionRegistral() : "");

            txtDomicilio.setReadOnly(false);
            comboSociedad.setReadOnly(false);
            txtTelEmergencia.setReadOnly(false);
            txtInscripcion.setReadOnly(false);
        }

        formNuevosDatos.add(txtDomicilio, comboSociedad, txtTelEmergencia, txtInscripcion);

        // BOTONERA-----------------------------------------------
        Button btnGuardar = new Button("Enviar Registro Legal", VaadinIcon.PAPERPLANE.create());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        if (yaRegistrada) {
            btnGuardar.setVisible(false);
            Span mensajeExito = new Span("Los datos legales y de constitución de su empresa ya han sido cargados con éxito.");
            mensajeExito.getStyle().set("color", "#009A3B").set("font-weight", "bold");
            add(datosExistentesCard, new H3("📋 Datos Legales"), formNuevosDatos, mensajeExito);
        } else {
            if (empresaData == null) {
                //si no hay emppresa tira un eerror
                VerticalLayout errorCard = new VerticalLayout();
                errorCard.getStyle().set("background-color", "#FFF5F5").set("border", "1px solid #FEB2B2").set("border-radius", "8px").set("padding", "1em");

                Span txtError = new Span("⚠️ No se encontró ninguna empresa pre-radicada vinculada a su cuenta de representante. ");
                txtError.getStyle().set("color", "#C53030").set("font-weight", "500");
                errorCard.add(txtError);

                // Agregamos los datos iniciales y el error debajo
                add(datosExistentesCard, errorCard);
            } else {
                // Si la empresa existe y esta lista para completar datos:
                btnGuardar.addClickListener(e -> {
                    if (txtDomicilio.isEmpty()) {
                        Notification.show("El domicilio legal es obligatorio para la radicación.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    if (comboSociedad.isEmpty()) {
                        Notification.show("Seleccione el tipo de sociedad de su empresa.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }

                    try {
                        empresaService.completarRegistroLegalEmpresa(
                                empresaData.getId(),
                                txtDomicilio.getValue(),
                                comboSociedad.getValue(),
                                txtTelEmergencia.getValue(),
                                txtInscripcion.getValue()
                        );

                        Notification.show("¡Registro final de empresa completado con éxito!", 4000, Notification.Position.TOP_CENTER)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        getUI().ifPresent(ui -> ui.navigate("mi-proyecto"));

                    } catch (Exception ex) {
                        Notification.show("Error al guardar en la base de datos: " + ex.getMessage(), 6000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        ex.printStackTrace();
                    }
                });
                add(datosExistentesCard, new H3("📋 Datos Legales"), formNuevosDatos, btnGuardar);
            }
        }
    } }
