package com.unrn.gpiv.views;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.common.Rol; // Asumo que tenés este Enum
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
public class LoginView extends VerticalLayout {

    private final EmpresaService empresaService;

    public LoginView(@Autowired EmpresaService empresaService) {
        this.empresaService = empresaService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f7fa");

        // --- TARJETA DE LOGIN (Mantenemos tu estilo que está flama) ---
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("400px");
        loginCard.getStyle().set("background-color", "white").set("padding", "2.5em")
                .set("border-radius", "15px").set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        loginCard.setAlignItems(Alignment.STRETCH);

        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        H2 titulo = new H2("Iniciar Sesión");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");

        TextField txtUsuario = new TextField("Correo electrónico o Usuario");
        txtUsuario.setPlaceholder("ejemplo@gmail.com");

        PasswordField txtPassword = new PasswordField("Contraseña");
        txtPassword.setPlaceholder("Ingresá tu contraseña");

        Button btnIngresar = new Button("INGRESAR");
        btnIngresar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnIngresar.getStyle().set("background-color", "#009A3B").set("font-weight", "bold");

        // --- LÓGICA DE INGRESO REAL ---
        btnIngresar.addClickListener(e -> {
            String email = txtUsuario.getValue().trim();
            String pass = txtPassword.getValue();

            try {
                // 1. IMPORTANTE: Usamos la clase padre 'Usuario' para que acepte Admin o Empresa
                // Y llamamos al nuevo método 'loginGeneral' que busca en toda la tabla
                Usuario usuario = empresaService.loginGeneral(email, pass);

                if (usuario != null) {
                    // 2. Guardamos el objeto en la sesión (Vaadin se encarga de saber qué hijo es)
                    VaadinSession.getCurrent().setAttribute("usuarioLogueado", usuario);

                    // 3. Redirigimos según el Rol (Tu lógica que ya estaba impecable)
                    if (usuario.getRol() == Rol.ADMIN) {
                        getUI().ifPresent(ui -> ui.navigate("admin/dashboard"));
                    } else {
                        getUI().ifPresent(ui -> ui.navigate("mi-proyecto"));
                    }
                }
            } catch (Exception ex) {
                // Si el service tira la excepción de "Credenciales inválidas", cae acá
                Notification n = Notification.show("Usuario o contraseña incorrectos");
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                n.setPosition(Notification.Position.MIDDLE);
            }
        });

        // --- LINKS INFERIORES ---
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        Button btnRegistro = new Button("Registrate y solicitá un lote", ev -> getUI().ifPresent(ui -> ui.navigate("registro")));
        btnRegistro.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        linksLayout.add(new Span("¿No tenés cuenta?"), btnRegistro);

        loginCard.add(logo, lineasBandera, titulo, txtUsuario, txtPassword, btnIngresar, linksLayout);
        add(loginCard);
    }
}

/*package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f7fa");

        // --- TARJETA DE LOGIN ---
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("400px");
        loginCard.getStyle().set("background-color", "white");
        loginCard.getStyle().set("padding", "2.5em");
        loginCard.getStyle().set("border-radius", "15px");
        loginCard.getStyle().set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");
        loginCard.setAlignItems(Alignment.STRETCH);

        // --- LOGO Y BANDERA ---
        Image logo = new Image("images/logo-parque.png", "Logo");
        logo.setHeight("70px");
        logo.getStyle().set("margin", "0 auto");

        Div lineasBandera = new Div();
        lineasBandera.setWidth("100px");
        lineasBandera.setHeight("4px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("margin", "0 auto 1.5em auto");

        // Título
        H2 titulo = new H2("Iniciar Sesión");
        titulo.getStyle().set("text-align", "center");
        titulo.getStyle().set("color", "#0063BE");
        titulo.getStyle().set("margin-top", "0");

        // Campos de texto
        TextField txtUsuario = new TextField("Correo electrónico o Usuario");
        txtUsuario.setPlaceholder("ejemplo@empresa.com");
        txtUsuario.setClearButtonVisible(true);

        PasswordField txtPassword = new PasswordField("Contraseña");
        txtPassword.setPlaceholder("Ingresá tu contraseña");

        // Botón de Ingresar
        Button btnIngresar = new Button("INGRESAR");
        btnIngresar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnIngresar.getStyle().set("background-color", "#009A3B");
        btnIngresar.getStyle().set("font-weight", "bold");
        btnIngresar.getStyle().set("margin-top", "1.5em");

        //logica mini sumilada para el btn ingreso
        btnIngresar.addClickListener(e -> {
            String usuario = txtUsuario.getValue().trim().toLowerCase();

            if (usuario.equals("empresa")) {
                // --- GUARDAMOS EL ROL EN LA SESIÓN ---
                com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("rol", "EMPRESA");
                getUI().ifPresent(ui -> ui.navigate("mi-proyecto"));

            } else if (usuario.equals("admin")) {
                // --- GUARDAMOS EL ROL EN LA SESIÓN ---
                com.vaadin.flow.server.VaadinSession.getCurrent().setAttribute("rol", "ADMIN");
                getUI().ifPresent(ui -> ui.navigate("admin/dashboard"));

            } else {
                com.vaadin.flow.component.notification.Notification.show("Usuario incorrecto");
            }
        });

        // --- SECCIÓN DE LINKS INFERIORES ---
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setAlignItems(Alignment.CENTER);
        linksLayout.setPadding(false);
        linksLayout.setSpacing(false);
        linksLayout.getStyle().set("margin-top", "1.5em");

        Button btnOlvido = new Button("¿Olvidaste tu contraseña?");
        btnOlvido.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnOlvido.getStyle().set("font-size", "0.85em");
        btnOlvido.getStyle().set("color", "#666");

        Span txtSeparador = new Span("────────  o  ────────");
        txtSeparador.getStyle().set("color", "#ccc");
        txtSeparador.getStyle().set("margin", "1em 0");

        Span txtNoCuenta = new Span("¿No tenés cuenta?");
        txtNoCuenta.getStyle().set("font-size", "0.9em");
        txtNoCuenta.getStyle().set("color", "#666");

        Button btnRegistro = new Button("Registrate y solicitá un lote", e -> {
            getUI().ifPresent(ui -> ui.navigate("registro"));
        });
        btnRegistro.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnRegistro.getStyle().set("font-weight", "bold");
        btnRegistro.getStyle().set("color", "#0063BE");

        linksLayout.add(btnOlvido, txtSeparador, txtNoCuenta, btnRegistro);

        // Agregamos todo a la tarjeta
        loginCard.add(logo, lineasBandera, titulo, txtUsuario, txtPassword, btnIngresar, linksLayout);

        add(loginCard);
    }
}*/