package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// Imports para los íconos nativos de Vaadin 24
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

@StyleSheet("https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600;700;900&display=swap")
@StyleSheet("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css")
@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Configuración de pantalla fluida
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#FFFFFF").set("min-height", "100vh");

        // Contenedor dinámico central
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContent.setAlignItems(Alignment.CENTER);
        mainContent.getStyle().set("padding", "30px 20px");

        // REMAKE: SECCIÓN DE TÍTULOS SUPERIORES

        // Nombre completo subido arriba en VERDE Río Negro (#009A3B) y tipografía pesada
        Span subtitulo = new Span("Sistema de Gestión del Parque Industrial de Viedma");
        subtitulo.getStyle()
                .set("font-size", "2.6em")
                .set("letter-spacing", "0.5px")
                .set("color", "#009A3B") // Combinando con la provincia
                .set("font-weight", "900")
                .set("text-align", "center")
                .set("margin-top", "15px");

        //Slogan institucional ubicado inmediatamente abajo
        Paragraph desc = new Paragraph("Parque Industrial de Viedma — Motor Productivo de la Comarca");
        desc.getStyle()
                .set("color", "#0063BE") // #0063BE azul para que resalte, o #009A3B verde)
                .set("font-size", "1.25em")
                .set("font-weight", "500")
                .set("margin-top", "5px");

        //Frase de acción destacada e impactante con el tamaño anterior
        Span preguntaText = new Span("¿Qué desea hacer hoy?");
        preguntaText.getStyle()
                .set("font-size", "2.2em")
                .set("color", "#1A202C")
                .set("font-weight", "800")
                .set("margin-top", "35px")
                .set("letter-spacing", "-0.5px");

        // Contenedor de la botonera inferior
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "1.5em");
        botonera.setWidthFull();
        botonera.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        botonera.setAlignItems(FlexComponent.Alignment.START);
        botonera.getStyle().set("gap", "40px");

        // HEADER INSTITUCIONAL (MENÚ HORIZONTAL TOTALMENTE CENTRADO)
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("border-bottom", "1px solid #E2E8F0")
                .set("padding", "15px 40px");

        Image logo = new Image("images/Enrepavi.png", "Logo del Parque Industrial");
        logo.setHeight("120px");

        HorizontalLayout leftBox = new HorizontalLayout(logo);
        leftBox.setPadding(false); leftBox.setSpacing(false);

        HorizontalLayout navMenu = new HorizontalLayout();
        navMenu.setSpacing(true);
        navMenu.getStyle().set("gap", "25px");

        String[] menuItems = {"Inicio", "El Parque", "Ubicación", "Beneficios"};
        VaadinIcon[] menuIcons = {VaadinIcon.HOME, VaadinIcon.FACTORY, VaadinIcon.MAP_MARKER, VaadinIcon.BAR_CHART};

        navMenu.getStyle()
                .set("position", "absolute")
                .set("left", "50%")
                .set("transform", "translateX(-50%)");

        for (int i = 0; i < menuItems.length; i++) {
            HorizontalLayout itemLayout = new HorizontalLayout();
            itemLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            itemLayout.setSpacing(false);
            itemLayout.getStyle()
                    .set("color", "#4A5568")
                    .set("cursor", "pointer")
                    .set("transition", "color 0.2s, transform 0.2s");

            Icon icon = menuIcons[i].create();
            icon.setSize("20px"); //tamaño de iconos!!
            icon.getStyle().set("margin-right", "8px");

            Span navLink = new Span(menuItems[i]);
            navLink.getStyle()
                    .set("font-weight", "600")
                    .set("font-size", "1.2em"); //tamaño de letras!

            itemLayout.add(icon, navLink);

            itemLayout.getElement().addEventListener("mouseover", e -> {
                itemLayout.getStyle().set("color", "#0063BE");
                itemLayout.getStyle().set("transform", "translateY(-2px)");
            });
            itemLayout.getElement().addEventListener("mouseout", e -> {
                itemLayout.getStyle().set("color", "#4A5568");
                itemLayout.getStyle().set("transform", "translateY(0px)");
            });

            //aca se cargan donde CLIQUEAS y te abre la pestaña seleccionada!
            String itemText = menuItems[i];
            itemLayout.getElement().addEventListener("click", e -> {
                if ("El Parque".equals(itemText)) {
                    mainContent.removeAll();
                    mainContent.add(new ElParqueComponent());
                }
                else if ("Ubicación".equals(itemText)) {
                    mainContent.removeAll();
                    mainContent.add(new UbicacionComponent());
                }
                else if ("Beneficios".equals(itemText)) {
                    mainContent.removeAll();
                    mainContent.add(new BeneficiosComponent()); // Inyecta las cards fiscales premium
                }
                else if ("Inicio".equals(itemText)) {
                    mainContent.removeAll();
                    mainContent.add(subtitulo, desc, preguntaText, botonera);
                }
            });

            navMenu.add(itemLayout);
        }

        Image bandera = new Image("images/logo-rn.png", "Bandera de Río Negro");
        bandera.setHeight("80px");
        bandera.getStyle().set("border-radius", "6px").set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.08)");

        HorizontalLayout rightBox = new HorizontalLayout(bandera);
        rightBox.setPadding(false); rightBox.setSpacing(false);
        rightBox.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        header.add(leftBox, navMenu, rightBox);
        header.setFlexGrow(1, leftBox);
        header.setFlexGrow(0, navMenu);
        header.setFlexGrow(1, rightBox);

        //MAQUETADO DE TARJETAS (SIMÉTRICO)
        String baseTransition = "background-color 0.2s, box-shadow 0.2s, transform 0.1s ease-in-out";

        VerticalLayout cardIngreso = new VerticalLayout();
        cardIngreso.setWidth("420px");
        cardIngreso.setAlignItems(FlexComponent.Alignment.CENTER);
        cardIngreso.getStyle()
                .set("border", "1px solid #E2E8F0")
                .set("border-top", "5px solid #009A3B")
                .set("border-radius", "12px")
                .set("padding", "35px 25px")
                .set("background-color", "#F8FAFC")
                .set("box-shadow", "0 4px 15px rgba(0, 0, 0, 0.03)");

        Span textTitleIngreso = new Span("Ingresar al sistema");
        textTitleIngreso.getStyle().set("font-weight", "700").set("font-size", "1.4em").set("color", "#1A202C");

        Span textSubIngreso = new Span("Para empresas ya radicadas y personal administrativo.");
        textSubIngreso.getStyle().set("color", "#718096").set("font-size", "0.95em").set("text-align", "center").set("margin", "12px 0 25px 0").set("height", "40px");

        Button btnEntrar = new Button("INGRESAR", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.getStyle()
                .set("background-color", "#009A3B")
                .set("padding", "1.6em 0")
                .set("font-weight", "700")
                .set("font-size", "0.95em")
                .set("border-radius", "50px")
                .set("box-shadow", "0 6px 15px rgba(0, 154, 59, 0.25)")
                .set("cursor", "pointer")
                .set("width", "280px")
                .set("transition", baseTransition);

        btnEntrar.getElement().addEventListener("mouseover", e -> {
            btnEntrar.getStyle().set("box-shadow", "0 10px 25px rgba(0, 154, 59, 0.4)");
            btnEntrar.getStyle().set("transform", "translateY(-3px)");
        });
        btnEntrar.getElement().addEventListener("mouseout", e -> {
            btnEntrar.getStyle().set("box-shadow", "0 6px 15px rgba(0, 154, 59, 0.25)");
            btnEntrar.getStyle().set("transform", "translateY(0px)");
        });
        cardIngreso.add(textTitleIngreso, textSubIngreso, btnEntrar);

        VerticalLayout cardRegistro = new VerticalLayout();
        cardRegistro.setWidth("420px");
        cardRegistro.setAlignItems(FlexComponent.Alignment.CENTER);
        cardRegistro.getStyle()
                .set("border", "1px solid #E2E8F0")
                .set("border-top", "5px solid #0063BE")
                .set("border-radius", "12px")
                .set("padding", "35px 25px")
                .set("background-color", "#F8FAFC")
                .set("box-shadow", "0 4px 15px rgba(0, 0, 0, 0.03)");

        Span textTitleRegistro = new Span("Nuevos proyectos / Registrarse");
        textTitleRegistro.getStyle().set("font-weight", "700").set("font-size", "1.4em").set("color", "#1A202C");

        Span textSubRegistro = new Span("Para emprendedores que buscan solicitar su primer lote.");
        textSubRegistro.getStyle().set("color", "#718096").set("font-size", "0.95em").set("text-align", "center").set("margin", "12px 0 25px 0").set("height", "40px");

        Button btnSolicitar = new Button("SOLICITAR LOTE", e -> getUI().ifPresent(ui -> ui.navigate("registro")));
        btnSolicitar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSolicitar.getStyle()
                .set("background-color", "#0063BE")
                .set("padding", "1.6em 0")
                .set("font-weight", "700")
                .set("font-size", "0.95em")
                .set("border-radius", "50px")
                .set("box-shadow", "0 6px 15px rgba(0, 99, 190, 0.25)")
                .set("cursor", "pointer")
                .set("width", "280px")
                .set("transition", baseTransition);

        btnSolicitar.getElement().addEventListener("mouseover", e -> {
            btnSolicitar.getStyle().set("box-shadow", "0 10px 25px rgba(0, 99, 190, 0.4)");
            btnSolicitar.getStyle().set("transform", "translateY(-3px)");
        });
        btnSolicitar.getElement().addEventListener("mouseout", e -> {
            btnSolicitar.getStyle().set("box-shadow", "0 6px 15px rgba(0, 99, 190, 0.25)");
            btnSolicitar.getStyle().set("transform", "translateY(0px)");
        });
        cardRegistro.add(textTitleRegistro, textSubRegistro, btnSolicitar);

        // Ensamblado del nuevo orden visual inicial
        botonera.add(cardIngreso, cardRegistro);
        mainContent.add(subtitulo, desc, preguntaText, botonera);

        // BARRA DE NOVEDADES (MARQUEE)
        HorizontalLayout barraAnuncios = new HorizontalLayout();
        barraAnuncios.setWidthFull();
        barraAnuncios.setHeight("55px");
        barraAnuncios.getStyle()
                .set("background-color", "#F7FAFC")
                .set("border-top", "1px solid #E2E8F0")
                .set("border-bottom", "1px solid #E2E8F0")
                .set("overflow", "hidden")
                .set("position", "relative")
                .set("align-items", "center");

        Div marqueeEngine = new Div();
        marqueeEngine.getStyle().set("display", "flex").set("width", "max-content").set("animation", "marqueeLoop 35s linear infinite");

        String[] novedades = {
                "📢 CONVOCATORIA ABIERTA: Presentación de Proyectos Productivos para el período 2026.",
                "🏭 RADICACIÓN EMPRESARIAL: La firma 'Alimentos Patagónicos' comenzó el movimiento de suelos en el Lote 14.",
                "⚡ INFRAESTRUCTURA MUNICIPAL: Finalizaron las obras de extensión de la red de gas de alta presión en el Sector B.",
                "🌱 REQUERIMIENTO AMBIENTAL: Se recuerda a las empresas radicadas presentar la declaración semestral de impacto.",
                "🤝 ENREPAVI INFORMA: Nueva mesa de asistencia técnica digital disponible para pymes industriales de la comarca."
        };

        for (int i = 0; i < 2; i++) {
            for (String texto : novedades) {
                Span item = new Span(texto);
                item.getStyle().set("padding", "0 50px").set("font-weight", "600").set("color", "#4A5568").set("white-space", "nowrap");
                marqueeEngine.add(item);
            }
        }

        barraAnuncios.add(marqueeEngine);
        marqueeEngine.getElement().executeJs(
                "const style = document.createElement('style');" +
                        "style.textContent = '@keyframes marqueeLoop { 0% { transform: translateX(0%); } 100% { transform: translateX(-50%); } }';" +
                        "document.head.appendChild(style);"
        );


        // FOOTER ROBUSTO DARK
        Div footerContainer = new Div();
        footerContainer.setWidthFull();
        footerContainer.getStyle()
                .set("background-color", "#1A202C")
                .set("padding", "45px 60px")
                .set("color", "#A0AEC0")
                .set("box-sizing", "border-box");

        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout colContacto = new VerticalLayout();
        colContacto.setPadding(false); colContacto.setSpacing(false);
        Span titleContacto = new Span("CONTACTO");
        titleContacto.getStyle().set("color", "#FFFFFF").set("font-weight", "700").set("margin-bottom", "12px");
        colContacto.add(titleContacto);

        String[] infoContacto = {
                "<span style='font-size:0.95em; margin-bottom:5px; display:inline-block;'><i class='fa fa-envelope' style='color:#009A3B;margin-right:8px'></i>admin@viedma.gov.ar</span>",
                "<span style='font-size:0.95em; margin-bottom:5px; display:inline-block;'><i class='fa fa-phone' style='color:#009A3B;margin-right:8px'></i>+54 (2920) 431400</span>",
                "<span style='font-size:0.95em; margin-bottom:5px; display:inline-block;'><i class='fa fa-map-marker-alt' style='color:#009A3B;margin-right:8px'></i>Viedma, Río Negro</span>"
        };
        for (String htmlText : infoContacto) {
            Span row = new Span();
            row.getElement().setProperty("innerHTML", htmlText);
            colContacto.add(row);
        }

        VerticalLayout colAccesos = new VerticalLayout();
        colAccesos.setPadding(false); colAccesos.setSpacing(false);
        Span titleAccesos = new Span("ACCESOS RÁPIDOS");
        titleAccesos.getStyle().set("color", "#FFFFFF").set("font-weight", "700").set("margin-bottom", "12px");
        colAccesos.add(titleAccesos, new Span("» El Parque Industrial"), new Span("» Ubicación de Lotes"), new Span("» Beneficios y Leyes"));
        colAccesos.getChildren().forEach(c -> { if(c != titleAccesos) c.getElement().getStyle().set("margin-bottom", "5px").set("font-size", "0.95em"); });

        VerticalLayout colSocial = new VerticalLayout();
        colSocial.setPadding(false); colSocial.setSpacing(false);
        Span titleSocial = new Span("SEGUINOS");
        titleSocial.getStyle().set("color", "#FFFFFF").set("font-weight", "700").set("margin-bottom", "12px");

        Span redesSpan = new Span();
        redesSpan.getElement().setProperty("innerHTML",
                "<i class='fab fa-facebook' style='font-size:24px;cursor:pointer;margin-right:15px'></i>" +
                        "<i class='fab fa-instagram' style='font-size:24px;cursor:pointer;margin-right:15px'></i>" +
                        "<i class='fab fa-linkedin' style='font-size:24px;cursor:pointer'></i>"
        );

        Span copyright = new Span("© 2026 SGPIV - PortalSurSoftware");
        copyright.getStyle().set("margin-top", "25px").set("font-size", "0.9em").set("color", "#718096").set("font-weight", "bold");

        colSocial.add(titleSocial, redesSpan, copyright);

        footerLayout.add(colContacto, colAccesos, colSocial);
        footerContainer.add(footerLayout);

        add(header, mainContent, barraAnuncios, footerContainer);
    }
}

/*FUNCIONA MUY MUY BIEN PERO QUIERO VER SI SE PUEDE MEJORAR
package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// ¡Ruta vacía y SIN layout! 100% independiente
@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Configuración general: fondo blanco y sin espacios raros
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#FFFFFF");

        // --- 1. HEADER INTEGRADO DIRECTAMENTE EN LA VISTA ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        // Logo de ENREPAVI a la izquierda
        Image logo = new Image("images/Enrepavi.png", "Logo del Parque Industrial");
        logo.setHeight("180px"); // Logo grande de presentación

        // CAMBIO ACA PARA PONER LA BANDERAAA -> Reemplazamos las líneas de CSS por el logo real de Río Negro
        Image bandera = new Image("images/logo-rn.png", "Bandera de Río Negro");
        bandera.setHeight("90px"); // Tamaño proporcional para que no tape al logo principal
        bandera.getStyle().set("border-radius", "6px"); // Esquinas redondeadas chetas
        bandera.getStyle().set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)"); // Sombrita de relieve sutil
        bandera.getStyle().set("margin-right", "20px"); // Separación del borde de la pantalla

        // Agregamos el logo y la bandera real al header
        header.add(logo, bandera);

        // --- 2. CUERPO CENTRAL ---
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContent.setAlignItems(Alignment.CENTER);

        H1 titulo = new H1("SGPIV");
        titulo.getStyle().set("font-size", "6em");
        titulo.getStyle().set("font-weight", "900");
        titulo.getStyle().set("color", "#000000");
        titulo.getStyle().set("margin", "0");

        Span subtitulo = new Span("Sistema de Gestión del Parque Industrial de Viedma");
        subtitulo.getStyle().set("font-size", "1.5em");
        subtitulo.getStyle().set("letter-spacing", "5px");
        subtitulo.getStyle().set("color", "#0063BE");
        subtitulo.getStyle().set("font-weight", "bold");

        Paragraph desc = new Paragraph("Parque Industrial de Viedma - Motor Productivo de la Comarca");
        desc.getStyle().set("color", "#666");
        desc.getStyle().set("margin-top", "1em");

        // --- SECCIÓN DE BOTONES ---
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setSpacing(true);

        Button btnEntrar = new Button("INGRESAR AL SISTEMA", e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.getStyle().set("background-color", "#009A3B");
        btnEntrar.getStyle().set("padding", "1.5em 2.5em");
        btnEntrar.getStyle().set("font-weight", "bold");
        btnEntrar.getStyle().set("border-radius", "50px");
        btnEntrar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 154, 59, 0.4)");
        btnEntrar.getStyle().set("cursor", "pointer");

        Button btnSolicitar = new Button("SOLICITAR LOTE (NUEVA EMPRESA)", e -> {
            getUI().ifPresent(ui -> ui.navigate("registro"));
        });
        btnSolicitar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSolicitar.getStyle().set("background-color", "#0063BE");
        btnSolicitar.getStyle().set("padding", "1.5em 2.5em");
        btnSolicitar.getStyle().set("font-weight", "bold");
        btnSolicitar.getStyle().set("border-radius", "50px");
        btnSolicitar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 99, 190, 0.4)");
        btnSolicitar.getStyle().set("cursor", "pointer");

        botonera.add(btnEntrar, btnSolicitar);
        mainContent.add(titulo, subtitulo, desc, botonera);

        // --- 3. FOOTER ---
        Div footer = new Div();
        footer.setWidthFull();
        footer.setHeight("10px");
        footer.getStyle().set("background-color", "#000000");
// --- 2.5 SECCIÓN DE NOVEDADES / ANUNCIOS (Marquee Automático) ---
        HorizontalLayout barraAnuncios = new HorizontalLayout();
        barraAnuncios.setWidthFull();
        barraAnuncios.setHeight("60px");
        barraAnuncios.getStyle()
                .set("background-color", "#F5F7FA") // Gris claro sutil
                .set("border-top", "2px solid #0063BE") // Línea superior con el azul de Río Negro
                .set("overflow", "hidden") // Esconde lo que se sale de la pantalla
                .set("position", "relative")
                .set("align-items", "center");

        // El contenedor contenedor móvil que se va a desplazar
        Div marqueeEngine = new Div();
        marqueeEngine.getStyle()
                .set("display", "flex")
                .set("width", "max-content")
                .set("animation", "marqueeLoop 30s linear infinite"); // ⏱️ Velocidad del desplazamiento

        // Lista de novedades del Parque Industrial de Viedma
        String[] novedades = {
                "📢 CONVOCATORIA ABIERTA: Presentación de Proyectos Productivos para el período 2026.",
                "🏭 RADICACIÓN EMPRESARIAL: La firma 'Alimentos Patagónicos' comenzó el movimiento de suelos en el Lote 14.",
                "⚡ INFRAESTRUCTURA MUNICIPAL: Finalizaron las obras de extensión de la red de gas de alta presión en el Sector B.",
                "🌱 REQUERIMIENTO AMBIENTAL: Se recuerda a las empresas radicadas presentar la declaración semestral de impacto.",
                "🤝 ENREPAVI INFORMA: Nueva mesa de asistencia técnica digital disponible para pymes industriales de la comarca."
        };

        // Cargamos los anuncios en la barra
        for (String texto : novedades) {
            Span item = new Span(texto);
            item.getStyle()
                    .set("padding", "0 60px") // Separación generosa entre anuncio y anuncio
                    .set("font-weight", "600")
                    .set("color", "#2D3748") // Gris oscuro profesional
                    .set("white-space", "nowrap")
                    .set("font-size", "1.05em");
            marqueeEngine.add(item);
        }

        // Duplicamos los anuncios atrás para que el bucle infinito sea fluido y no se corte nunca
        for (String texto : novedades) {
            Span item = new Span(texto);
            item.getStyle()
                    .set("padding", "0 60px")
                    .set("font-weight", "600")
                    .set("color", "#2D3748")
                    .set("white-space", "nowrap")
                    .set("font-size", "1.05em");
            marqueeEngine.add(item);
        }

        barraAnuncios.add(marqueeEngine);

        // 🚀 LA MAGIA: Inyectamos dinámicamente la animación CSS al navegador en microsegundos
        marqueeEngine.getElement().executeJs(
                "const style = document.createElement('style');" +
                        "style.textContent = '@keyframes marqueeLoop { 0% { transform: translateX(0%); } 100% { transform: translateX(-50%); } }';" +
                        "document.head.appendChild(style);"
        );

        // Sumamos TODO a la pantalla (incluido el header con el logo)
        add(header, mainContent, footer);
    }
}*/



/*funciona perrfecto este home view!
package com.unrn.gpiv.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

// ¡Ruta vacía y SIN layout! 100% independiente
@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        // Configuración general: fondo blanco y sin espacios raros
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#FFFFFF");

        // --- 1. HEADER INTEGRADO DIRECTAMENTE EN LA VISTA ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Image logo = new Image("images/Enrepavi.png", "Logo del Parque Industrial");
        logo.setHeight("180px"); // Logo grande de presentación

        Div lineasBandera = new Div();
        lineasBandera.setWidth("150px");
        lineasBandera.setHeight("5px");
        lineasBandera.getStyle().set("background", "linear-gradient(to right, #0063BE 33%, #FFFFFF 33%, #FFFFFF 66%, #009A3B 66%)");
        lineasBandera.getStyle().set("border", "1px solid #eee");

        header.add(logo, lineasBandera);

        // --- 2. CUERPO CENTRAL ---
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContent.setAlignItems(Alignment.CENTER);

        H1 titulo = new H1("SGPIV");
        titulo.getStyle().set("font-size", "6em");
        titulo.getStyle().set("font-weight", "900");
        titulo.getStyle().set("color", "#000000");
        titulo.getStyle().set("margin", "0");

        Span subtitulo = new Span("Sistema de Gestión del Parque Industrial de Viedma");
        subtitulo.getStyle().set("font-size", "1.5em");
        subtitulo.getStyle().set("letter-spacing", "5px");
        subtitulo.getStyle().set("color", "#0063BE");
        subtitulo.getStyle().set("font-weight", "bold");

        Paragraph desc = new Paragraph("Parque Industrial de Viedma - Motor Productivo de la Comarca");
        desc.getStyle().set("color", "#666");
        desc.getStyle().set("margin-top", "1em");

        // --- SECCIÓN DE BOTONES ---
        HorizontalLayout botonera = new HorizontalLayout();
        botonera.getStyle().set("margin-top", "2em");
        botonera.setSpacing(true);

        Button btnEntrar = new Button("INGRESAR AL SISTEMA", e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.getStyle().set("background-color", "#009A3B");
        btnEntrar.getStyle().set("padding", "1.5em 2.5em");
        btnEntrar.getStyle().set("font-weight", "bold");
        btnEntrar.getStyle().set("border-radius", "50px");
        btnEntrar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 154, 59, 0.4)");
        btnEntrar.getStyle().set("cursor", "pointer");

        Button btnSolicitar = new Button("SOLICITAR LOTE (NUEVA EMPRESA)", e -> {
            getUI().ifPresent(ui -> ui.navigate("registro"));
        });
        btnSolicitar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSolicitar.getStyle().set("background-color", "#0063BE");
        btnSolicitar.getStyle().set("padding", "1.5em 2.5em");
        btnSolicitar.getStyle().set("font-weight", "bold");
        btnSolicitar.getStyle().set("border-radius", "50px");
        btnSolicitar.getStyle().set("box-shadow", "0 4px 15px rgba(0, 99, 190, 0.4)");
        btnSolicitar.getStyle().set("cursor", "pointer");

        botonera.add(btnEntrar, btnSolicitar);
        mainContent.add(titulo, subtitulo, desc, botonera);

        // --- 3. FOOTER ---
        Div footer = new Div();
        footer.setWidthFull();
        footer.setHeight("10px");
        footer.getStyle().set("background-color", "#000000");

        // Sumamos TODO a la pantalla (incluido el header con el logo)
        add(header, mainContent, footer);
    }
}*/