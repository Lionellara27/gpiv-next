package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ElParqueComponent extends VerticalLayout {

    public ElParqueComponent() {
        // Configuraciones raíz del componente
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("font-family", "'Montserrat', sans-serif");
        getStyle().set("background-color", "#FFFFFF");


        // FILA 1: PRESENTACIÓN (TEXTO IZQUIERDA | IMAGEN DERECHA) - FONDO BLANCO

        HorizontalLayout filaPresentacion = new HorizontalLayout();
        filaPresentacion.setWidthFull();
        filaPresentacion.setSpacing(false);
        filaPresentacion.getStyle().set("padding", "60px 80px");
        filaPresentacion.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Texto Izquierda (55%)
        VerticalLayout txtPresentacion = new VerticalLayout();
        txtPresentacion.setPadding(false);
        txtPresentacion.setSpacing(true);
        txtPresentacion.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-right", "50px")
                .set("box-sizing", "border-box");

        H2 subTitulo = new H2("El Parque Industrial, Productivo y Logístico de Viedma");
        subTitulo.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph p1 = new Paragraph("Somos un Parque Industrial, Productivo y Logístico ubicado en la ciudad de Viedma, capital de la provincia de Río Negro. Nuestro predio se encuentra estratégicamente localizado sobre la Ruta Provincial N° 1 (antigua Ruta 300, camino al balneario El Cóndor), a unos 3 km del centro de la ciudad y con proximidad a las vías del Ferrocarril General Roca. Contamos con una superficie original que actualmente se encuentra en pleno proceso de expansión, habiéndose gestionado la expropiación de 25 hectáreas adicionales para duplicar su capacidad y albergar a nuevas empresas.");
        p1.getStyle().set("color", "#2D3748").set("font-size", "1.05em").set("line-height", "1.6");

        Paragraph p2 = new Paragraph("Nuestro objetivo es diversificar la matriz económica de la capital rionegrina, sumándole a su tradicional perfil administrativo un fuerte motor de producción, manufactura y servicios. El parque es desarrollado y administrado por el ENREPAVI (Ente de Reconversión del Parque Industrial de Viedma), un organismo mixto cuyo directorio está integrado por representantes del sector público (el Gobierno de la Provincia de Río Negro y la Municipalidad de Viedma) y el sector privado (a través de la Cámara de Comercio de Viedma).");
        p2.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.6");

        txtPresentacion.add(subTitulo, p1, p2);

        //Columna Imagen Derecha (45%)
        VerticalLayout imgPresentacion = new VerticalLayout();
        imgPresentacion.setPadding(false);
        imgPresentacion.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");

        Image fotoEntrada = new Image("images/parque industrial cartel.webp", "Entrada al Parque Industrial Viedma");
        fotoEntrada.setWidth("100%");
        fotoEntrada.getStyle()
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.06)")
                .set("height", "380px")
                .set("object-fit", "cover");
        imgPresentacion.add(fotoEntrada);

        filaPresentacion.add(txtPresentacion, imgPresentacion);



        // FILA 2: OBJETIVO GENERAL (IMAGEN IZQUIERDA | TEXTO DERECHA) - FONDO GRIS

        HorizontalLayout filaObjGeneral = new HorizontalLayout();
        filaObjGeneral.setWidthFull();
        filaObjGeneral.setSpacing(false);
        filaObjGeneral.getStyle().set("padding", "60px 80px").set("background-color", "#F8FAFC");
        filaObjGeneral.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Imagen Izquierda (45%)
        VerticalLayout imgObjGeneral = new VerticalLayout();
        imgObjGeneral.setPadding(false);
        imgObjGeneral.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");

        Image fotoObjetivos = new Image("images/laburo parque.jpg", "Objetivos Generales");
        fotoObjetivos.setWidth("100%");
        fotoObjetivos.getStyle()
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.06)")
                .set("height", "220px") // Más petisa porque el texto que acompaña es corto
                .set("object-fit", "cover");
        imgObjGeneral.add(fotoObjetivos);

        // Columna Texto Derecha (55%)
        VerticalLayout txtObjGeneral = new VerticalLayout();
        txtObjGeneral.setPadding(false);
        txtObjGeneral.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-left", "50px")
                .set("box-sizing", "border-box");

        VerticalLayout panelGeneral = new VerticalLayout();
        panelGeneral.setWidthFull();
        panelGeneral.setPadding(true);
        panelGeneral.getStyle()
                .set("border-left", "5px solid #0063BE")
                .set("background-color", "#FFFFFF")
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.02)");

        H3 titleGeneral = new H3("🎯 OBJETIVO GENERAL");
        titleGeneral.getStyle().set("color", "#1A202C").set("margin", "0 0 10px 0").set("font-weight", "700");

        Paragraph descGeneral = new Paragraph("Impulsar el desarrollo económico, productivo y logístico de la región, promoviendo la radicación de empresas y consolidando a la capital rionegrina como un polo industrial altamente competitivo.");
        descGeneral.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("margin", "0");

        panelGeneral.add(titleGeneral, descGeneral);
        txtObjGeneral.add(panelGeneral);

        filaObjGeneral.add(imgObjGeneral, txtObjGeneral);


        // FILA 3: OBJETIVOS ESPECÍFICOS (TEXTO IZQUIERDA | IMAGEN DERECHA) - FONDO BLANCO

        HorizontalLayout filaObjEspecificos = new HorizontalLayout();
        filaObjEspecificos.setWidthFull();
        filaObjEspecificos.setSpacing(false);
        filaObjEspecificos.getStyle().set("padding", "60px 80px");
        filaObjEspecificos.setAlignItems(FlexComponent.Alignment.CENTER);

        // 📝 Columna Texto Izquierda (55%)
        VerticalLayout txtObjEspecificos = new VerticalLayout();
        txtObjEspecificos.setPadding(false);
        txtObjEspecificos.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-right", "50px")
                .set("box-sizing", "border-box");

        VerticalLayout panelEspecificos = new VerticalLayout();
        panelEspecificos.setWidthFull();
        panelEspecificos.setPadding(true);
        panelEspecificos.getStyle()
                .set("border-left", "5px solid #009A3B")
                .set("background-color", "#F8FAFC")
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.02)");

        H3 titleEspecificos = new H3("📋 OBJETIVOS ESPECÍFICOS");
        titleEspecificos.getStyle().set("color", "#1A202C").set("margin", "0 0 15px 0").set("font-weight", "700");
        panelEspecificos.add(titleEspecificos);

        String[] objetivos = {
                "• Atraer inversiones estratégicas de alcance nacional y regional, priorizando sectores clave como la agroindustria, la manufactura, las empresas de servicios, aserraderos y la construcción.",
                "• Diversificar la matriz económica local, potenciando el desarrollo del sector privado para la generación de empleo genuino, calificado y de calidad.",
                "• Potenciar el perfil logístico regional, proyectando al predio como un nodo estratégico de transferencia y distribución de carga a gran escala, aprovechando la conectividad vial y el desarrollo de infraestructura ferroviaria.",
                "• Fortalecer las cadenas de valor, incentivando el procesamiento local y la incorporación de valor agregado a las materias primas e insumos provenientes de las unidades productivas de la región.",
                "• Impulsar el desarrollo de pymes y nuevos emprendimientos, promoviendo entornos propicios y herramientas que faciliten la escalabilidad de los proyectos locales.",
                "• Fomentar la sinergia institucional y tecnológica, articulando activamente con el sector público, cámaras empresariales y el sistema educativo y científico (UNRN, UNCo, INTI, IDEVI) para acompañar la modernización y la innovación productiva.",
                "• Brindar herramientas de competitividad, ofreciendo un marco robusto de incentivos, servicios esenciales y exenciones impositivas provinciales que estimulen la inversión a largo plazo.",
                "• Captar oportunidades de escala energética, adecuando la oferta de infraestructura y servicios industriales para abastecer las demandas derivadas de los nuevos desarrollos energéticos y proyectos estratégicos de la provincia."
        };

        for (String obj : objetivos) {
            Span line = new Span(obj);
            line.getStyle()
                    .set("color", "#4A5568")
                    .set("margin-bottom", "10px")
                    .set("font-weight", "500")
                    .set("font-size", "0.95em")
                    .set("line-height", "1.5");
            panelEspecificos.add(line);
        }
        txtObjEspecificos.add(panelEspecificos);

        // Columna Imagen Derecha (45%)
        VerticalLayout imgObjEspecificos = new VerticalLayout();
        imgObjEspecificos.setPadding(false);
        imgObjEspecificos.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");

        Image fotoTrabajadores = new Image("images/laburo parque 3.jpg", "Trabajadores del Parque Industrial Viedma");
        fotoTrabajadores.setWidth("100%");
        fotoTrabajadores.getStyle()
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.06)")
                .set("height", "580px") // Altura estirada para emparejar la grilla de los 8 puntos largos
                .set("object-fit", "cover");
        imgObjEspecificos.add(fotoTrabajadores);

        filaObjEspecificos.add(txtObjEspecificos, imgObjEspecificos);



        // FILA 4: AUTORIDADES (TEXTO IZQUIERDA | IMAGEN DERECHA) - FONDO GRIS

        HorizontalLayout filaAutoridades = new HorizontalLayout();
        filaAutoridades.setWidthFull();
        filaAutoridades.setSpacing(false);
        filaAutoridades.getStyle().set("padding", "60px 80px").set("background-color", "#F8FAFC");
        filaAutoridades.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Texto Izquierda (55%)
        VerticalLayout txtAutoridades = new VerticalLayout();
        txtAutoridades.setPadding(false);
        txtAutoridades.setSpacing(true);
        txtAutoridades.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-right", "50px")
                .set("box-sizing", "border-box");

        H3 titleAutoridades = new H3("Estructura de Autoridades y Gestión del ENREPAVI");
        titleAutoridades.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 10px 0").set("font-size", "1.75em");

        // Grid Ejecutivos
        HorizontalLayout gridEjecutivos = new HorizontalLayout();
        gridEjecutivos.setWidthFull();
        gridEjecutivos.getStyle().set("gap", "20px").set("margin-bottom", "15px");

        VerticalLayout cardPresi = new VerticalLayout();
        cardPresi.setWidth("50%"); cardPresi.setPadding(true); cardPresi.setSpacing(false);
        cardPresi.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#FFFFFF").set("min-width", "0");
        Span cargoP = new Span("PRESIDENTE DEL DIRECTORIO");
        cargoP.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreP = new Span("Marcelo Ruiz");
        nombreP.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgP = new Span("Representante electo del Sector Privado");
        orgP.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardPresi.add(cargoP, nombreP, orgP);

        VerticalLayout cardGerente = new VerticalLayout();
        cardGerente.setWidth("50%"); cardGerente.setPadding(true); cardGerente.setSpacing(false);
        cardGerente.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#FFFFFF").set("min-width", "0");
        Span cargoG = new Span("GERENTE / ADMINISTRADOR");
        cargoG.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreG = new Span("Martín Lemos");
        nombreG.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgG = new Span("Funcionario Público Provincial / Exdirector Cámara de Comercio");
        orgG.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardGerente.add(cargoG, nombreG, orgG);

        gridEjecutivos.add(cardPresi, cardGerente);

        // Bloques Explicativos
        VerticalLayout bloquesExplicativos = new VerticalLayout();
        bloquesExplicativos.setWidthFull();
        bloquesExplicativos.setPadding(false);
        bloquesExplicativos.getStyle().set("gap", "20px");

        VerticalLayout bloqueDirectorio = new VerticalLayout();
        bloqueDirectorio.setPadding(true);
        bloqueDirectorio.getStyle().set("background-color", "#FFFFFF").set("border-radius", "8px").set("border", "1px solid #E2E8F0");

        Span tDir = new Span("🏢 CONFORMACIÓN DEL DIRECTORIO MIXTO (8 Miembros Ad-Honorem)");
        tDir.getStyle().set("font-weight", "700").set("color", "#1A202C").set("font-size", "1.05em").set("margin-bottom", "8px");

        Paragraph descDir = new Paragraph("El Directorio dicta las pautas operacionales generales del predio y se reúne de manera asamblearia cada tres meses. Está compuesto de forma simétrica por:");
        descDir.getStyle().set("color", "#4A5568").set("font-size", "0.95em").set("margin-bottom", "8px");

        Span dPublicos = new Span("• 4 Representantes del Sector Público: Un miembro del Ejecutivo Provincial, uno del Legislativo Provincial, uno del Ejecutivo Municipal y uno del Legislativo Municipal.");
        dPublicos.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("margin-bottom", "4px").set("font-size", "0.9em").set("font-weight", "500");

        Span dPrivados = new Span("• 4 Representantes del Sector Privado: Un miembro de la Cámara de Comercio de Viedma, uno por las grandes empresas industriales, uno por las medianas empresas y uno por las pequeñas unidades productivas radicadas.");
        dPrivados.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("font-size", "0.9em").set("font-weight", "500");

        bloqueDirectorio.add(tDir, descDir, dPublicos, dPrivados);

        HorizontalLayout filaComisiones = new HorizontalLayout();
        filaComisiones.setWidthFull();
        filaComisiones.getStyle().set("gap", "20px");

        VerticalLayout colComite = new VerticalLayout();
        colComite.setWidth("50%"); colComite.setPadding(true);
        colComite.getStyle().set("background-color", "#FFFFFF").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tComite = new Span("⚡ COMITÉ EJECUTIVO REDUCIDO");
        tComite.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descComite = new Paragraph("Comisión ágil y compacta integrada por el Presidente, el Vicepresidente y un Vocal del directorio. Es la encargada de resolver las contingencias de gestión y firmar resoluciones de urgencia entre los cierres de las asambleas trimestrales.");
        descComite.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colComite.add(tComite, descComite);

        VerticalLayout colEquipo = new VerticalLayout();
        colEquipo.setWidth("50%"); colEquipo.setPadding(true);
        colEquipo.getStyle().set("background-color", "#FFFFFF").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tEquipo = new Span("👥 EQUIPO DE GESTIÓN OPERATIVA DIARIA");
        tEquipo.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descEquipo = new Paragraph("Estructura operativa de planta fija compuesta por 4 empleados públicos: el Gerente General a cargo de la firma administrativa, un Técnico Civil dedicado a evaluar la viabilidad preliminar estructural de los proyectos civiles presentados, y dos Administrativos permanentes.");
        descEquipo.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colEquipo.add(tEquipo, descEquipo);

        filaComisiones.add(colComite, colEquipo);
        bloquesExplicativos.add(bloqueDirectorio, filaComisiones);

        txtAutoridades.add(titleAutoridades, gridEjecutivos, bloquesExplicativos);

        // Columna Imagen Derecha (45%)
        VerticalLayout imgAutoridades = new VerticalLayout();
        imgAutoridades.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");
        imgAutoridades.setPadding(false);

        // Ajuste del tamaño de la última parte: Altura estricta para acompañar todo el texto de autoridades largo sin salirse
        Image fotoAutoridades = new Image("images/parque foto de arriba.jpg", "Directorio y Mesa Mixta ENREPAVI");
        fotoAutoridades.setWidth("100%");
        fotoAutoridades.getStyle()
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.06)")
                .set("height", "540px")
                .set("object-fit", "cover");
        imgAutoridades.add(fotoAutoridades);

        filaAutoridades.add(txtAutoridades, imgAutoridades);


        //ENSAMBLADO VERTICAL EN RITMO DE CASCADA INTERCALADA COMPLETA
        add(filaPresentacion, filaObjGeneral, filaObjEspecificos, filaAutoridades);
    }
}

/*package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ElParqueComponent extends VerticalLayout {

    public ElParqueComponent() {
        // Configuraciones raíz del componente
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("font-family", "'Montserrat', sans-serif");
        getStyle().set("background-color", "#FFFFFF");

        // Estilo premium para las imágenes (Bordes curvos, sombra flotante y bloqueo de tamaño)
        String baseImageStyle = "border-radius: 16px; box-shadow: 0 10px 25px rgba(0, 0, 0, 0.06); max-width: 100%; height: auto; display: block;";
        // Estilo unificado para los efectos de las tres imágenes
        String transitionStyle = "border-radius: 16px; box-shadow: 0 10px 25px rgba(0, 0, 0, 0.06);";


        // =====================================================================
        // 🏭 BLOQUE 1: PRESENTACIÓN (TEXTO IZQUIERDA | IMAGEN DERECHA)
        // =====================================================================
        HorizontalLayout bloquePresentacion = new HorizontalLayout();
        bloquePresentacion.setWidthFull();
        bloquePresentacion.setSpacing(false);
        bloquePresentacion.getStyle().set("padding", "60px 80px");
        bloquePresentacion.setAlignItems(FlexComponent.Alignment.CENTER);

        // 📝 Columna Texto Izquierda (55% Clavado)
        VerticalLayout txtPresentacion = new VerticalLayout();
        txtPresentacion.setPadding(false);
        txtPresentacion.setSpacing(true);
        // 🎯 TRUCO MAESTRO: Clavamos el tamaño para que el texto no empuje a la foto
        txtPresentacion.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-right", "50px")
                .set("box-sizing", "border-box");

        H2 subTitulo = new H2("El Parque Industrial, Productivo y Logístico de Viedma");
        subTitulo.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph p1 = new Paragraph("Somos un Parque Industrial, Productivo y Logístico ubicado en la ciudad de Viedma, capital de la provincia de Río Negro. Nuestro predio se encuentra estratégicamente localizado sobre la Ruta Provincial N° 1 (antigua Ruta 300, camino al balneario El Cóndor), a unos 3 km del centro de la ciudad y con proximidad a las vías del Ferrocarril General Roca. Contamos con una superficie original que actualmente se encuentra en pleno proceso de expansión, habiéndose gestionado la expropiación de 25 hectáreas adicionales para duplicar su capacidad y albergar a nuevas empresas.");
        p1.getStyle().set("color", "#2D3748").set("font-size", "1.05em").set("line-height", "1.6");

        Paragraph p2 = new Paragraph("Nuestro objetivo es diversificar la matriz económica de la capital rionegrina, sumándole a su tradicional perfil administrativo un fuerte motor de producción, manufactura y servicios. El parque es desarrollado y administrado por el ENREPAVI (Ente de Reconversión del Parque Industrial de Viedma), un organismo mixto cuyo directorio está integrado por representantes del sector público (el Gobierno de la Provincia de Río Negro y la Municipalidad de Viedma) y el sector privado (a través de la Cámara de Comercio de Viedma).");
        p2.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.6");

        txtPresentacion.add(subTitulo, p1, p2);

        // 🖼️ Columna Imagen Derecha (45% Clavado)
        VerticalLayout imgPresentacion = new VerticalLayout();
        imgPresentacion.setPadding(false);
        imgPresentacion.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");

        Image fotoEntrada = new Image("images/parque industrial cartel.webp", "Entrada al Parque Industrial Viedma");
        fotoEntrada.setWidthFull(); // 🎯 OBLIGA a la foto a medir el 100% de su columna (45% total)
        fotoEntrada.getElement().setAttribute("style", baseImageStyle);
        imgPresentacion.add(fotoEntrada);

        bloquePresentacion.add(txtPresentacion, imgPresentacion);


        // =====================================================================
        // 🎯 BLOQUE 2: OBJETIVOS (IMAGEN IZQUIERDA | TEXTO DERECHA) - FONDO SUBTLE
        // =====================================================================
        HorizontalLayout bloqueObjetivos = new HorizontalLayout();
        bloqueObjetivos.setWidthFull();
        bloqueObjetivos.setSpacing(false);
        bloqueObjetivos.getStyle().set("padding", "60px 80px").set("background-color", "#F8FAFC");
        bloqueObjetivos.setAlignItems(FlexComponent.Alignment.CENTER);

        // 🖼️ Columna Imagen Izquierda (45% Clavado)
        VerticalLayout imgObjetivos = new VerticalLayout();
        imgObjetivos.setPadding(false);
        imgObjetivos.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");

        Image fotoObjetivos = new Image("images/laburo parque 3.jpg", "Objetivos de Radicación de Empresas");
        fotoObjetivos.setWidthFull(); // 🎯 Se adapta sí o sí al tamaño asignado
        fotoObjetivos.getElement().setAttribute("style", baseImageStyle);
        imgObjetivos.add(fotoObjetivos);

        // 📝 Columna Texto Derecha (55% Clavado)
        VerticalLayout txtObjetivos = new VerticalLayout();
        txtObjetivos.setPadding(false);
        txtObjetivos.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-left", "50px")
                .set("gap", "25px")
                .set("box-sizing", "border-box");

        // Panel del Objetivo General
        VerticalLayout panelGeneral = new VerticalLayout();
        panelGeneral.setWidthFull();
        panelGeneral.setPadding(true);
        panelGeneral.getStyle()
                .set("border-left", "5px solid #0063BE")
                .set("background-color", "#FFFFFF")
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.01)");

        H3 titleGeneral = new H3("🎯 OBJETIVO GENERAL");
        titleGeneral.getStyle().set("color", "#1A202C").set("margin", "0 0 10px 0").set("font-weight", "700");

        Paragraph descGeneral = new Paragraph("Impulsar el desarrollo económico, productivo y logístico de la región, promoviendo la radicación de empresas y consolidando a la capital rionegrina como un polo industrial altamente competitivo.");
        descGeneral.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("margin", "0");
        panelGeneral.add(titleGeneral, descGeneral);

        // Panel de los Objetivos Específicos
        VerticalLayout panelEspecificos = new VerticalLayout();
        panelEspecificos.setWidthFull();
        panelEspecificos.setPadding(true);
        panelEspecificos.getStyle()
                .set("border-left", "5px solid #009A3B")
                .set("background-color", "#FFFFFF")
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.01)");

        H3 titleEspecificos = new H3("📋 OBJETIVOS ESPECÍFICOS");
        titleEspecificos.getStyle().set("color", "#1A202C").set("margin", "0 0 15px 0").set("font-weight", "700");
        panelEspecificos.add(titleEspecificos);

        String[] objetivos = {
                "• Atraer inversiones estratégicas de alcance nacional y regional, priorizando sectores clave como la agroindustria, la manufactura, las empresas de servicios, aserraderos y la construcción.",
                "• Diversificar la matriz económica local, potenciando el desarrollo del sector privado para la generación de empleo genuino, calificado y de calidad.",
                "• Potenciar el perfil logístico regional, proyectando al predio como un nodo estratégico de transferencia y distribution de carga a gran escala, aprovechando la conectividad vial y el desarrollo de infraestructura ferroviaria.",
                "• Fortalecer las cadenas de valor, incentivando el procesamiento local y la incorporación de valor agregado a las materias primas e insumos provenientes de las unidades productivas de la región.",
                "• Impulsar el desarrollo de pymes y nuevos emprendimientos, promoviendo entornos propicios y herramientas que faciliten la escalabilidad de los proyectos locales.",
                "• Fomentar la sinergia institucional y tecnológica, articulando activamente con el sector público, cámaras empresariales y el sistema educativo y científico (UNRN, UNCo, INTI, IDEVI) para acompañar la modernización y la innovación productiva.",
                "• Brindar herramientas de competitividad, ofreciendo un marco robusto de incentivos, servicios esenciales y exenciones impositivas provinciales que estimulen la inversión a largo plazo.",
                "• Captar oportunidades de escala energética, adecuando la oferta de infraestructura y servicios industriales para abastecer las demandas derivadas de los nuevos desarrollos energéticos y proyectos estratégicos de la provincia."
        };

        for (String obj : objetivos) {
            Span line = new Span(obj);
            line.getStyle()
                    .set("color", "#4A5568")
                    .set("margin-bottom", "10px")
                    .set("font-weight", "500")
                    .set("font-size", "0.95em")
                    .set("line-height", "1.5");
            panelEspecificos.add(line);
        }

        txtObjetivos.add(panelGeneral, panelEspecificos);
        bloqueObjetivos.add(imgObjetivos, txtObjetivos);


        // =====================================================================
        // 👥 BLOQUE 3: AUTORIDADES (TEXTO IZQUIERDA | IMAGEN DERECHA)
        // =====================================================================
        HorizontalLayout bloqueAutoridades = new HorizontalLayout();
        bloqueAutoridades.setWidthFull();
        bloqueAutoridades.setSpacing(false);
        bloqueAutoridades.getStyle().set("padding", "60px 80px");
        bloqueAutoridades.setAlignItems(FlexComponent.Alignment.CENTER);

        // 📝 Columna Texto Izquierda (55% Clavado)
        VerticalLayout txtAutoridades = new VerticalLayout();
        txtAutoridades.setPadding(false);
        txtAutoridades.setSpacing(true);
        txtAutoridades.getStyle()
                .set("width", "55%")
                .set("min-width", "55%")
                .set("max-width", "55%")
                .set("padding-right", "50px")
                .set("box-sizing", "border-box");

        H3 titleAutoridades = new H3("Estructura de Autoridades y Gestión del ENREPAVI");
        titleAutoridades.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 10px 0").set("font-size", "1.75em");

        // Fila de Cards Ejecutivas
        HorizontalLayout gridEjecutivos = new HorizontalLayout();
        gridEjecutivos.setWidthFull();
        gridEjecutivos.getStyle().set("gap", "20px").set("margin-bottom", "15px");

        VerticalLayout cardPresi = new VerticalLayout();
        cardPresi.setWidth("50%"); cardPresi.setPadding(true); cardPresi.setSpacing(false);
        cardPresi.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC").set("min-width", "0");
        Span cargoP = new Span("PRESIDENTE DEL DIRECTORIO");
        cargoP.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreP = new Span("Marcelo Ruiz");
        nombreP.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgP = new Span("Representante electo del Sector Privado");
        orgP.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardPresi.add(cargoP, nombreP, orgP);

        VerticalLayout cardGerente = new VerticalLayout();
        cardGerente.setWidth("50%"); cardGerente.setPadding(true); cardGerente.setSpacing(false);
        cardGerente.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC").set("min-width", "0");
        Span cargoG = new Span("GERENTE / ADMINISTRADOR");
        cargoG.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreG = new Span("Martín Lemos");
        nombreG.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgG = new Span("Funcionario Público Provincial / Exdirector Cámara de Comercio");
        orgG.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardGerente.add(cargoG, nombreG, orgG);

        gridEjecutivos.add(cardPresi, cardGerente);

        // Bloques Explicativos Inferiores
        VerticalLayout bloquesExplicativos = new VerticalLayout();
        bloquesExplicativos.setWidthFull();
        bloquesExplicativos.setPadding(false);
        bloquesExplicativos.getStyle().set("gap", "20px");

        VerticalLayout bloqueDirectorio = new VerticalLayout();
        bloqueDirectorio.setPadding(true);
        bloqueDirectorio.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");

        Span tDir = new Span("🏢 CONFORMACIÓN DEL DIRECTORIO MIXTO (8 Miembros Ad-Honorem)");
        tDir.getStyle().set("font-weight", "700").set("color", "#1A202C").set("font-size", "1.05em").set("margin-bottom", "8px");

        Paragraph descDir = new Paragraph("El Directorio dicta las pautas operacionales generales del predio y se reúne de manera asamblearia cada tres meses. Está compuesto de forma simétrica por:");
        descDir.getStyle().set("color", "#4A5568").set("font-size", "0.95em").set("margin-bottom", "8px");

        Span dPublicos = new Span("• 4 Representantes del Sector Público: Un miembro del Ejecutivo Provincial, uno del Legislativo Provincial, uno del Ejecutivo Municipal y uno del Legislativo Municipal.");
        dPublicos.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("margin-bottom", "4px").set("font-size", "0.9em").set("font-weight", "500");

        Span dPrivados = new Span("• 4 Representantes del Sector Privado: Un miembro de la Cámara de Comercio de Viedma, uno por las grandes empresas industriales, uno por las medianas empresas y uno por las pequeñas unidades productivas radicadas.");
        dPrivados.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("font-size", "0.9em").set("font-weight", "500");

        bloqueDirectorio.add(tDir, descDir, dPublicos, dPrivados);

        HorizontalLayout filaComisiones = new HorizontalLayout();
        filaComisiones.setWidthFull();
        filaComisiones.getStyle().set("gap", "20px");

        VerticalLayout colComite = new VerticalLayout();
        colComite.setWidth("50%"); colComite.setPadding(true);
        colComite.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tComite = new Span("⚡ COMITÉ EJECUTIVO REDUCIDO");
        tComite.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descComite = new Paragraph("Comisión ágil y compacta integrada por el Presidente, el Vicepresidente y un Vocal del directorio. Es la encargada de resolver las contingencias de gestión y firmar resoluciones de urgencia entre los cierres de las asambleas trimestrales.");
        descComite.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colComite.add(tComite, descComite);

        VerticalLayout colEquipo = new VerticalLayout();
        colEquipo.setWidth("50%"); colEquipo.setPadding(true);
        colEquipo.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tEquipo = new Span("👥 EQUIPO DE GESTIÓN OPERATIVA DIARIA");
        tEquipo.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descEquipo = new Paragraph("Estructura operativa de planta fija compuesta por 4 empleados públicos: el Gerente General a cargo de la firma administrativa, un Técnico Civil dedicado a evaluar la viabilidad preliminar estructural de los proyectos civiles presentados, y dos Administrativos permanentes.");
        descEquipo.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colEquipo.add(tEquipo, descEquipo);

        filaComisiones.add(colComite, colEquipo);
        bloquesExplicativos.add(bloqueDirectorio, filaComisiones);

        txtAutoridades.add(titleAutoridades, gridEjecutivos, bloquesExplicativos);

        // 🖼️ Columna Imagen Derecha (45% Clavado)
        VerticalLayout imgAutoridades = new VerticalLayout();
        imgAutoridades.getStyle()
                .set("width", "45%")
                .set("min-width", "45%")
                .set("max-width", "45%")
                .set("box-sizing", "border-box");
        imgAutoridades.setPadding(false);

        Image fotoAutoridades = new Image("images/parque foto de arriba.jpg", "Directorio y Mesa Mixta ENREPAVI");
        fotoAutoridades.setWidthFull(); // 🎯 Clavadísima en su lugar
        fotoAutoridades.getElement().setAttribute("style", transitionStyle);
        imgAutoridades.add(fotoAutoridades);

        bloqueAutoridades.add(txtAutoridades, imgAutoridades);

        // =====================================================================
        // 🚀 ENSAMBLADO VERTICAL FINAL
        // =====================================================================
        add(bloquePresentacion, bloqueObjetivos, bloqueAutoridades);
    }
}*/
/*package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image; // 🟢 IMPORTANTE: Importamos el componente de imagen
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ElParqueComponent extends VerticalLayout {

    public ElParqueComponent() {
        // Configuraciones raíz del componente
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("font-family", "'Montserrat', sans-serif");
        getStyle().set("background-color", "#FFFFFF");

        // Estilo unificado para los efectos de las tres imágenes
        String transitionStyle = "border-radius: 16px; box-shadow: 0 10px 25px rgba(0, 0, 0, 0.06);";

        // =====================================================================
        // 🏭 BLOQUE 1: PRESENTACIÓN (TEXTO IZQUIERDA | IMAGEN DERECHA)
        // =====================================================================
        HorizontalLayout bloquePresentacion = new HorizontalLayout();
        bloquePresentacion.setWidthFull();
        bloquePresentacion.setSpacing(false);
        bloquePresentacion.getStyle().set("padding", "60px 80px");
        bloquePresentacion.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Texto Izquierda (55%)
        VerticalLayout txtPresentacion = new VerticalLayout();
        txtPresentacion.setWidth("55%");
        txtPresentacion.setPadding(false);
        txtPresentacion.setSpacing(true);

        H2 subTitulo = new H2("El Parque Industrial, Productivo y Logístico de Viedma");
        subTitulo.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph p1 = new Paragraph("Somos un Parque Industrial, Productivo y Logístico ubicado en la ciudad de Viedma, capital de la provincia de Río Negro. Nuestro predio se encuentra estratégicamente localizado sobre la Ruta Provincial N° 1 (antigua Ruta 300, camino al balneario El Cóndor), a unos 3 km del centro de la ciudad y con proximidad a las vías del Ferrocarril General Roca. Contamos con una superficie original que actualmente se encuentra en pleno proceso de expansión, habiéndose gestionado la expropiación de 25 hectáreas adicionales para duplicar su capacidad y albergar a nuevas empresas.");
        p1.getStyle().set("color", "#2D3748").set("font-size", "1.05em").set("line-height", "1.6");

        Paragraph p2 = new Paragraph("Nuestro objetivo es diversificar la matriz económica de la capital rionegrina, sumándole a su tradicional perfil administrativo un fuerte motor de producción, manufactura y servicios. El parque es desarrollado y administrado por el ENREPAVI (Ente de Reconversión del Parque Industrial de Viedma), un organismo mixto cuyo directorio está integrado por representantes del sector público (el Gobierno de la Provincia de Río Negro y la Municipalidad de Viedma) y el sector privado (a través de la Cámara de Comercio de Viedma).");
        p2.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.6");

        txtPresentacion.add(subTitulo, p1, p2);

        // Columna Imagen Derecha (45%)
        VerticalLayout imgPresentacion = new VerticalLayout();
        imgPresentacion.setWidth("45%");
        imgPresentacion.getStyle().set("padding-left", "40px");

        Image fotoEntrada = new Image("images/parque industrial cartel.webp", "Entrada al Parque Industrial Viedma");
        fotoEntrada.setWidth("100%");
        fotoEntrada.getElement().setAttribute("style", transitionStyle);
        imgPresentacion.add(fotoEntrada);

        bloquePresentacion.add(txtPresentacion, imgPresentacion);


        // =====================================================================
        // 🎯 BLOQUE 2: OBJETIVOS (IMAGEN IZQUIERDA | TEXTO DERECHA) - FONDO SUBTLE
        // =====================================================================
        HorizontalLayout bloqueObjetivos = new HorizontalLayout();
        bloqueObjetivos.setWidthFull();
        bloqueObjetivos.setSpacing(false);
        bloqueObjetivos.getStyle().set("padding", "60px 80px").set("background-color", "#F8FAFC");
        bloqueObjetivos.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Imagen Izquierda (45%)
        VerticalLayout imgObjetivos = new VerticalLayout();
        imgObjetivos.setWidth("45%");
        imgObjetivos.getStyle().set("padding-right", "40px");

        Image fotoObjetivos = new Image("images/laburo parque 3.jpg", "Objetivos de Radicación de Empresas");
        fotoObjetivos.setWidth("100%");
        fotoObjetivos.getElement().setAttribute("style", transitionStyle);
        imgObjetivos.add(fotoObjetivos);

        // Columna Texto Derecha (55%)
        VerticalLayout txtObjetivos = new VerticalLayout();
        txtObjetivos.setWidth("55%");
        txtObjetivos.setPadding(false);
        txtObjetivos.getStyle().set("gap", "25px");

        // Panel del Objetivo General
        VerticalLayout panelGeneral = new VerticalLayout();
        panelGeneral.setWidthFull();
        panelGeneral.setPadding(true);
        panelGeneral.getStyle()
                .set("border-left", "5px solid #0063BE")
                .set("background-color", "#FFFFFF") // Resalta blanco sobre el fondo grisáceo
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.01)");

        H3 titleGeneral = new H3("🎯 OBJETIVO GENERAL");
        titleGeneral.getStyle().set("color", "#1A202C").set("margin", "0 0 10px 0").set("font-weight", "700");

        Paragraph descGeneral = new Paragraph("Impulsar el desarrollo económico, productivo y logístico de la región, promoviendo la radicación de empresas y consolidando a la capital rionegrina como un polo industrial altamente competitivo.");
        descGeneral.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("margin", "0");
        panelGeneral.add(titleGeneral, descGeneral);

        // Panel de los Objetivos Específicos
        VerticalLayout panelEspecificos = new VerticalLayout();
        panelEspecificos.setWidthFull();
        panelEspecificos.setPadding(true);
        panelEspecificos.getStyle()
                .set("border-left", "5px solid #009A3B")
                .set("background-color", "#FFFFFF")
                .set("border-radius", "0 8px 8px 0")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.01)");

        H3 titleEspecificos = new H3("📋 OBJETIVOS ESPECÍFICOS");
        titleEspecificos.getStyle().set("color", "#1A202C").set("margin", "0 0 15px 0").set("font-weight", "700");
        panelEspecificos.add(titleEspecificos);

        String[] objetivos = {
                "• Atraer inversiones estratégicas de alcance nacional y regional, priorizando sectores clave como la agroindustria, la manufactura, las empresas de servicios, aserraderos y la construcción.",
                "• Diversificar la matriz económica local, potenciando el desarrollo del sector privado para la generación de empleo genuino, calificado y de calidad.",
                "• Potenciar el perfil logístico regional, proyectando al predio como un nodo estratégico de transferencia y distribución de carga a gran escala, aprovechando la conectividad vial y el desarrollo de infraestructura ferroviaria.",
                "• Fortalecer las cadenas de valor, incentivando el procesamiento local y la incorporación de valor agregado a las materias primas e insumos provenientes de las unidades productivas de la región.",
                "• Impulsar el desarrollo de pymes y nuevos emprendimientos, promoviendo entornos propicios y herramientas que faciliten la escalabilidad de los proyectos locales.",
                "• Fomentar la sinergia institucional y tecnológica, articulando activamente con el sector público, cámaras empresariales y el sistema educativo y científico (UNRN, UNCo, INTI, IDEVI) para acompañar la modernización y la innovación productiva.",
                "• Brindar herramientas de competitividad, ofreciendo un marco robusto de incentivos, servicios esenciales y exenciones impositivas provinciales que estimulen la inversión a largo plazo.",
                "• Captar oportunidades de escala energética, adecuando la oferta de infraestructura y servicios industriales para abastecer las demandas derivadas de los nuevos desarrollos energéticos y proyectos estratégicos de la provincia."
        };

        for (String obj : objetivos) {
            Span line = new Span(obj);
            line.getStyle()
                    .set("color", "#4A5568")
                    .set("margin-bottom", "10px")
                    .set("font-weight", "500")
                    .set("font-size", "0.95em")
                    .set("line-height", "1.5");
            panelEspecificos.add(line);
        }

        txtObjetivos.add(panelGeneral, panelEspecificos);
        bloqueObjetivos.add(imgObjetivos, txtObjetivos);


        // =====================================================================
        // 👥 BLOQUE 3: AUTORIDADES (TEXTO IZQUIERDA | IMAGEN DERECHA)
        // =====================================================================
        HorizontalLayout bloqueAutoridades = new HorizontalLayout();
        bloqueAutoridades.setWidthFull();
        bloqueAutoridades.setSpacing(false);
        bloqueAutoridades.getStyle().set("padding", "60px 80px");
        bloqueAutoridades.setAlignItems(FlexComponent.Alignment.CENTER);

        // Columna Texto Izquierda (55%)
        VerticalLayout txtAutoridades = new VerticalLayout();
        txtAutoridades.setWidth("55%");
        txtAutoridades.setPadding(false);
        txtAutoridades.setSpacing(true);

        H3 titleAutoridades = new H3("Estructura de Autoridades y Gestión del ENREPAVI");
        titleAutoridades.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 10px 0").set("font-size", "1.75em");

        // Fila 1: Cargos Ejecutivos Directos (Simetría del 50% cada una)
        HorizontalLayout gridEjecutivos = new HorizontalLayout();
        gridEjecutivos.setWidthFull();
        gridEjecutivos.getStyle().set("gap", "20px").set("margin-bottom", "15px");

        // Card Presidente
        VerticalLayout cardPresi = new VerticalLayout();
        cardPresi.setWidth("50%"); cardPresi.setPadding(true); cardPresi.setSpacing(false);
        cardPresi.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC");
        Span cargoP = new Span("PRESIDENTE DEL DIRECTORIO");
        cargoP.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreP = new Span("Marcelo Ruiz");
        nombreP.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgP = new Span("Representante electo del Sector Privado");
        orgP.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardPresi.add(cargoP, nombreP, orgP);

        // Card Gerente
        VerticalLayout cardGerente = new VerticalLayout();
        cardGerente.setWidth("50%"); cardGerente.setPadding(true); cardGerente.setSpacing(false);
        cardGerente.getStyle().set("border", "1px solid #E2E8F0").set("border-radius", "8px").set("background-color", "#F8FAFC");
        Span cargoG = new Span("GERENTE / ADMINISTRADOR");
        cargoG.getStyle().set("color", "#0063BE").set("font-size", "0.8em").set("font-weight", "700").set("letter-spacing", "1px");
        Span nombreG = new Span("Martín Lemos");
        nombreG.getStyle().set("color", "#1A202C").set("font-size", "1.15em").set("font-weight", "700").set("margin", "4px 0");
        Span orgG = new Span("Funcionario Público Provincial / Exdirector Cámara de Comercio");
        orgG.getStyle().set("color", "#718096").set("font-size", "0.9em").set("font-weight", "500");
        cardGerente.add(cargoG, nombreG, orgG);

        gridEjecutivos.add(cardPresi, cardGerente);

        // Fila 2: Bloques Explicativos de la Estructura Organizativa
        VerticalLayout bloquesExplicativos = new VerticalLayout();
        bloquesExplicativos.setWidthFull();
        bloquesExplicativos.setPadding(false);
        bloquesExplicativos.getStyle().set("gap", "20px");

        // Bloque Directorio Mixto
        VerticalLayout bloqueDirectorio = new VerticalLayout();
        bloqueDirectorio.setPadding(true);
        bloqueDirectorio.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");

        Span tDir = new Span("🏢 CONFORMACIÓN DEL DIRECTORIO MIXTO (8 Miembros Ad-Honorem)");
        tDir.getStyle().set("font-weight", "700").set("color", "#1A202C").set("font-size", "1.05em").set("margin-bottom", "8px");

        Paragraph descDir = new Paragraph("El Directorio dicta las pautas operacionales generales del predio y se reúne de manera asamblearia cada tres meses. Está compuesto de forma simétrica por:");
        descDir.getStyle().set("color", "#4A5568").set("font-size", "0.95em").set("margin-bottom", "8px");

        Span dPublicos = new Span("• 4 Representantes del Sector Público: Un miembro del Ejecutivo Provincial, uno del Legislativo Provincial, uno del Ejecutivo Municipal y uno del Legislativo Municipal.");
        dPublicos.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("margin-bottom", "4px").set("font-size", "0.9em").set("font-weight", "500");

        Span dPrivados = new Span("• 4 Representantes del Sector Privado: Un miembro de la Cámara de Comercio de Viedma, uno por las grandes empresas industriales, uno por las medianas empresas y uno por las pequeñas unidades productivas radicadas.");
        dPrivados.getStyle().set("color", "#4A5568").set("padding-left", "15px").set("font-size", "0.9em").set("font-weight", "500");

        bloqueDirectorio.add(tDir, descDir, dPublicos, dPrivados);

        // Fila de comisiones internas a dos columnas
        HorizontalLayout filaComisiones = new HorizontalLayout();
        filaComisiones.setWidthFull();
        filaComisiones.getStyle().set("gap", "20px");

        // Comité Ejecutivo Reducido
        VerticalLayout colComite = new VerticalLayout();
        colComite.setWidth("50%"); colComite.setPadding(true);
        colComite.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tComite = new Span("⚡ COMITÉ EJECUTIVO REDUCIDO");
        tComite.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descComite = new Paragraph("Comisión ágil y compacta integrada por el Presidente, el Vicepresidente y un Vocal del directorio. Es la encargada de resolver las contingencias de gestión y firmar resoluciones de urgencia entre los cierres de las asambleas trimestrales.");
        descComite.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colComite.add(tComite, descComite);

        // Equipo de Gestión Operativa Diaria
        VerticalLayout colEquipo = new VerticalLayout();
        colEquipo.setWidth("50%"); colEquipo.setPadding(true);
        colEquipo.getStyle().set("background-color", "#FAFAFA").set("border-radius", "8px").set("border", "1px solid #E2E8F0");
        Span tEquipo = new Span("👥 EQUIPO DE GESTIÓN OPERATIVA DIARIA");
        tEquipo.getStyle().set("font-weight", "700").set("color", "#1A202C").set("margin-bottom", "6px").set("font-size", "0.95em");
        Paragraph descEquipo = new Paragraph("Estructura operativa de planta fija compuesta por 4 empleados públicos: el Gerente General a cargo de la firma administrativa, un Técnico Civil dedicado a evaluar la viabilidad preliminar estructural de los proyectos civiles presentados, y dos Administrativos permanentes.");
        descEquipo.getStyle().set("color", "#4A5568").set("font-size", "0.9em").set("line-height", "1.5");
        colEquipo.add(tEquipo, descEquipo);

        filaComisiones.add(colComite, colEquipo);
        bloquesExplicativos.add(bloqueDirectorio, filaComisiones);

        txtAutoridades.add(titleAutoridades, gridEjecutivos, bloquesExplicativos);

        // Columna Imagen Derecha (45%)
        VerticalLayout imgAutoridades = new VerticalLayout();
        imgAutoridades.setWidth("45%");
        imgAutoridades.getStyle().set("padding-left", "40px");

        Image fotoAutoridades = new Image("images/parque foto de arriba.jpg", "Directorio y Mesa Mixta ENREPAVI");
        fotoAutoridades.setWidth("100%");
        fotoAutoridades.getElement().setAttribute("style", transitionStyle);
        imgAutoridades.add(fotoAutoridades);

        bloqueAutoridades.add(txtAutoridades, imgAutoridades);

        // =====================================================================
        // 🚀 ENSAMBLADO VERTICAL FINAL EN CASCADA INTERCALADA
        // =====================================================================
        add(bloquePresentacion, bloqueObjetivos, bloqueAutoridades);
    }
}*/