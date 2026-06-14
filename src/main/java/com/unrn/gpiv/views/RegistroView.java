package com.unrn.gpiv.views;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("registro")
public class RegistroView extends VerticalLayout {

    private TextField txtNombre = new TextField("Nombre Completo");
    private TextField txtDni = new TextField("DNI");
    private TextField txtCuit = new TextField("CUIT Personal");
    private TextField txtTelefono = new TextField("Teléfono de contacto");
    private EmailField txtCorreo = new EmailField("Correo electrónico");
    private PasswordField txtPassword = new PasswordField("Contraseña");

    private Binder<RepresentanteEmpresa> binder = new Binder<>(RepresentanteEmpresa.class);

    public RegistroView(@Autowired EmpresaService empresaService) {
        // --- CONFIGURACIÓN DE PANTALLA ---
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-y", "auto");
        getStyle().set("background-color", "#f5f7fa");

        configurarValidaciones();

        // --- TARJETA DE REGISTRO ---
        VerticalLayout registroCard = new VerticalLayout();
        registroCard.setWidth("500px");
        registroCard.getStyle().set("background-color", "white");
        registroCard.getStyle().set("padding", "2.5em");
        registroCard.getStyle().set("border-radius", "15px");
        registroCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        registroCard.getStyle().set("margin", "3em 0");
        registroCard.setAlignItems(Alignment.STRETCH);

        // --- DISEÑO Y TÍTULOS ---
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        H2 titulo = new H2("Solicitud de Lote");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");

        Paragraph subtitulo = new Paragraph("Paso 1: ¡Bienvenid@! Creá tu cuenta para iniciar el proyecto.");
        subtitulo.getStyle().set("text-align", "center");
        subtitulo.getStyle().set("color", "#666");

        RadioButtonGroup<String> tipoEntidad = new RadioButtonGroup<>();
        tipoEntidad.setLabel("¿Bajo qué figura te vas a registrar?");
        tipoEntidad.setItems("Persona Física (Monotributo/Autónomo)", "Persona Jurídica (SRL, SA, etc.)");
        tipoEntidad.setValue("Persona Física (Monotributo/Autónomo)");
        tipoEntidad.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        // Placeholders
        txtNombre.setPlaceholder("Ej: Pedro Pérez / Metalúrgica Viedma SRL");
        txtDni.setPlaceholder("Sin puntos");
        txtCuit.setPlaceholder("Sin guiones");

        // --- BOTÓN CON LÓGICA MEJORADA ---
        Button btnCrearCuenta = new Button("CREAR CUENTA Y CONTINUAR");
        btnCrearCuenta.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCrearCuenta.getStyle().set("background-color", "#009A3B");
        btnCrearCuenta.getStyle().set("margin-top", "1.5em");

        btnCrearCuenta.addClickListener(e -> {
            RepresentanteEmpresa nuevoRep = new RepresentanteEmpresa();

            if (binder.writeBeanIfValid(nuevoRep)) {
                // REGLA DE ORO: El username es el email
                nuevoRep.setUsername(nuevoRep.getEmail());

                try {
                    // Guardamos usando el Service que tiene las validaciones
                    RepresentanteEmpresa guardado = empresaService.registrarRepresentante(nuevoRep);

                    // Guardamos en sesión y notificamos éxito (Verde)
                    VaadinSession.getCurrent().setAttribute("usuarioLogueado", guardado);

                    Notification notification = Notification.show("¡Cuenta creada correctamente!", 3000, Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // Navegamos al formulario del proyecto
                    getUI().ifPresent(ui -> ui.navigate("formulario-proyecto"));

                } catch (IllegalArgumentException ex) {
                    // CAPTURAMOS EL ERROR DE DUPLICADO (Rojo)
                    Notification notification = Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } catch (Exception ex) {
                    Notification.show("Error inesperado: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Por favor, revisá los campos marcados en rojo.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        Button btnIrLogin = new Button("Ingresar acá", ev -> getUI().ifPresent(ui -> ui.navigate("login")));
        btnIrLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        linksLayout.add(new Span("¿Ya tenés cuenta?"), btnIrLogin);

        registroCard.add(logo, lineasBandera, titulo, subtitulo, tipoEntidad,
                txtNombre, txtDni, txtCuit, txtTelefono, txtCorreo, txtPassword,
                btnCrearCuenta, linksLayout);

        add(registroCard);
    }

    private void configurarValidaciones() {
        binder.forField(txtNombre)
                .asRequired("El nombre es obligatorio")
                .bind(RepresentanteEmpresa::getNombreCompleto, RepresentanteEmpresa::setNombreCompleto);

        binder.forField(txtDni)
                .asRequired("DNI requerido")
                .withValidator(dni -> dni.length() >= 7, "DNI inválido")
                .bind(RepresentanteEmpresa::getDni, RepresentanteEmpresa::setDni);

        binder.forField(txtCuit)
                .asRequired("CUIT requerido")
                .bind(RepresentanteEmpresa::getCuitPersonal, RepresentanteEmpresa::setCuitPersonal);

        binder.forField(txtTelefono)
                .asRequired("Teléfono requerido")
                .bind(RepresentanteEmpresa::getTelefono, RepresentanteEmpresa::setTelefono);

        // --- EL QUE FALTABA: BINDEO DEL CORREO ---
        binder.forField(txtCorreo)
                .asRequired("El correo es obligatorio")
                .withValidator(new com.vaadin.flow.data.validator.EmailValidator("Formato de correo inválido"))
                .bind(RepresentanteEmpresa::getEmail, RepresentanteEmpresa::setEmail);

        binder.forField(txtPassword)
                .asRequired("La contraseña es obligatoria")
                .withValidator(p -> p.length() >= 8, "Mínimo 8 caracteres")
                .bind(RepresentanteEmpresa::getPassword, RepresentanteEmpresa::setPassword);
    }
}

/*
package com.unrn.gpiv.views;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("registro")
public class RegistroView extends VerticalLayout {

    // Componentes del formulario
    private TextField txtNombre = new TextField("Nombre Completo");
    private TextField txtDni = new TextField("DNI");
    private TextField txtCuit = new TextField("CUIT Personal");
    private TextField txtTelefono = new TextField("Teléfono de contacto");
    private EmailField txtCorreo = new EmailField("Correo electrónico");
    private PasswordField txtPassword = new PasswordField("Contraseña");

    private Binder<RepresentanteEmpresa> binder = new Binder<>(RepresentanteEmpresa.class);

    public RegistroView(@Autowired EmpresaService empresaService) {
// --- CONFIGURACIÓN DE POSICIONAMIENTO ---
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        // QUITAMOS el JustifyContentMode.CENTER
        // AGREGAMOS scroll y fondo
        getStyle().set("overflow-y", "auto");
        getStyle().set("background-color", "#f5f7fa");

        configurarValidaciones();

        // --- TARJETA DE REGISTRO ---
        VerticalLayout registroCard = new VerticalLayout();
        registroCard.setWidth("500px");
        registroCard.getStyle().set("background-color", "white");
        registroCard.getStyle().set("padding", "2.5em");
        registroCard.getStyle().set("border-radius", "15px");
        registroCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");

        // MÁRGENES PARA QUE NO SE PEGUE ARRIBA NI ABAJO
        registroCard.getStyle().set("margin-top", "3em");
        registroCard.getStyle().set("margin-bottom", "3em");

        registroCard.setAlignItems(Alignment.STRETCH);

        // --- LOGO Y BANDERA ---
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        H2 titulo = new H2("Solicitud de Lote");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");
        titulo.getStyle().set("margin-top", "0");

        Paragraph subtitulo = new Paragraph("Paso 1: \n" +
                "¡Bienvenid@! Para poder solicitar un lote e iniciar tu proyecto, primero necesitamos crear tu cuenta en el sistema.");
        subtitulo.getStyle().set("text-align", "center");
        subtitulo.getStyle().set("color", "#666");

        // --- SELECTOR (Estético) ---
        RadioButtonGroup<String> tipoEntidad = new RadioButtonGroup<>();
        tipoEntidad.setLabel("¿Bajo qué figura te vas a registrar?");
        tipoEntidad.setItems("Persona Física (Monotributo/Autónomo)", "Persona Jurídica (SRL, SA, etc.)");
        tipoEntidad.setValue("Persona Física (Monotributo/Autónomo)");

        // 1. Magia para ponerlos UNO ABAJO DEL OTRO (Vertical)
        tipoEntidad.addThemeVariants(com.vaadin.flow.component.radiobutton.RadioGroupVariant.LUMO_VERTICAL);

        // 2. Magia para poner el texto de las opciones en NEGRITA
        tipoEntidad.getStyle().set("font-weight", "bold");

        // Placeholders
        txtNombre.setPlaceholder("Ej: Pedro Pérez / Metalúrgica Viedma SRL");
        txtDni.setPlaceholder("Sin puntos");
        txtCuit.setPlaceholder("Sin guiones");
        txtTelefono.setPlaceholder("Ej: 2920445566");
        txtCorreo.setPlaceholder("ejemplo@gmail.com");

        // --- BOTÓN PRINCIPAL CON LÓGICA ---
        Button btnCrearCuenta = new Button("CREAR CUENTA Y CONTINUAR");
        btnCrearCuenta.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCrearCuenta.getStyle().set("background-color", "#009A3B");
        btnCrearCuenta.getStyle().set("margin-top", "1.5em");

        btnCrearCuenta.addClickListener(e -> {
            RepresentanteEmpresa nuevoRep = new RepresentanteEmpresa();

            if (binder.writeBeanIfValid(nuevoRep)) {
                // Seteamos los datos de Usuario (Herencia)
                nuevoRep.setUsername(txtCorreo.getValue());
                nuevoRep.setEmail(txtCorreo.getValue());
                nuevoRep.setPassword(txtPassword.getValue()); // El service debería encriptarla

                try {
                    // 1. Guardamos en la Base de Datos
                    RepresentanteEmpresa guardado = empresaService.registrarRepresentante(nuevoRep);

                    // 2. IMPORTANTE: Guardamos en Sesión para la siguiente pantalla
                    VaadinSession.getCurrent().setAttribute("usuarioLogueado", guardado);

                    Notification.show("Cuenta creada con éxito");
                    getUI().ifPresent(ui -> ui.navigate("formulario-proyecto"));

                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage());
                }
            } else {
                Notification.show("Por favor, complete todos los campos correctamente");
            }
        });

        // --- VOLVER AL LOGIN ---
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        linksLayout.add(new Span("¿Ya tenés cuenta?"), new Button("Ingresar acá", ev -> getUI().ifPresent(ui -> ui.navigate("login"))));

        // --- ARMADO ---
        registroCard.add(logo, lineasBandera, titulo, subtitulo, tipoEntidad,
                txtNombre, txtDni, txtCuit, txtTelefono, txtCorreo, txtPassword,
                btnCrearCuenta, linksLayout);

        add(registroCard);
    }

    private void configurarValidaciones() {
        binder.forField(txtNombre).asRequired("El nombre es obligatorio").bind(RepresentanteEmpresa::getNombreCompleto, RepresentanteEmpresa::setNombreCompleto);
        binder.forField(txtDni).asRequired("DNI requerido").bind(RepresentanteEmpresa::getDni, RepresentanteEmpresa::setDni);
        binder.forField(txtCuit).asRequired("CUIT requerido").bind(RepresentanteEmpresa::getCuitPersonal, RepresentanteEmpresa::setCuitPersonal);
        binder.forField(txtTelefono).asRequired("Teléfono requerido").bind(RepresentanteEmpresa::getTelefono, RepresentanteEmpresa::setTelefono);
        binder.forField(txtPassword).asRequired("La contraseña es obligatoria").withValidator(p -> p.length() >= 8, "Mínimo 8 caracteres").bind(RepresentanteEmpresa::getPassword, RepresentanteEmpresa::setPassword);
    }
}*/