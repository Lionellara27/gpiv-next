package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image; // 🎯 COMPONENTE PARA TU ARCHIVO WEBP
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BeneficiosComponent extends HorizontalLayout {

    public BeneficiosComponent() {
        // Configuración de sección partida (Texto izquierda, Foto derecha)
        setWidthFull();
        setSpacing(false);
        getStyle().set("padding", "60px 80px");
        getStyle().set("font-family", "'Montserrat', sans-serif");
        getStyle().set("background-color", "#FFFFFF");
        setAlignItems(Alignment.START);


        //COLUMNA IZQUIERDA: BENEFICIOS FISCALES REALES DE VIEDMA

        VerticalLayout colIzquierda = new VerticalLayout();
        colIzquierda.setWidth("55%"); // Un toque más ancha para las explicaciones
        colIzquierda.setPadding(false);
        colIzquierda.setSpacing(true);

        H2 titleBeneficios = new H2("Régimen de Promoción Industrial");
        titleBeneficios.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph descBeneficios = new Paragraph("La radicación de empresas en el Parque Industrial de Viedma (ENREPAVI) cuenta con un esquema de incentivos diseñado para bajar los costos operativos iniciales, fomentar la inversión productiva y potenciar la competitividad de las plantas industriales de la comarca.");
        descBeneficios.getStyle().set("color", "#2D3748").set("font-size", "1.1em").set("line-height", "1.6");

        // Bloque Municipal
        H3 titleMuni = new H3("🏢 Exenciones Municipales (Viedma):");
        titleMuni.getStyle().set("color", "#009A3B").set("font-weight", "700").set("margin-top", "20px");

        Paragraph descMuni = new Paragraph(
                "• Exención del 100% en la Tasa de Inspección, Seguridad e Higiene.\n" +
                        "• Exención del 100% en Derechos de Construcción para el montaje de la planta.\n" +
                        "• Exención de Tasas por Servicios Municipales y de Catastro durante las fases críticas de radicación."
        );
        descMuni.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.7");

        // Bloque Provincial
        H3 titleProv = new H3("🏛️ Exenciones Provinciales (Ley de Promoción Industrial de Río Negro):");
        titleProv.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin-top", "25px");

        Paragraph descProv = new Paragraph(
                "• Exención total del Impuesto sobre los Ingresos Brutos derivados de la actividad productiva.\n" +
                        "• Exención del Impuesto de Sellos en contratos de adjudicación de lotes, obras y constitución de sociedades.\n" +
                        "• Exención del Impuesto Inmobiliario Industrial sobre el predio asignado dentro del Parque."
        );
        descProv.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.7");

        colIzquierda.add(titleBeneficios, descBeneficios, titleMuni, descMuni, titleProv, descProv);

        // COLUMNA DERECHA: FOTO WEBP

        VerticalLayout colDerecha = new VerticalLayout();
        colDerecha.setWidth("45%");
        colDerecha.setPadding(false);
        colDerecha.getStyle().set("margin-left", "40px");

        // 🎯 Cargamos la foto que vas a meter en src/main/resources/META-INF/resources/images/beneficios.webp
        Image fotoBeneficios = new Image("images/foto-parqueIndustrial.webp", "Predio Productivo Viedma");
        fotoBeneficios.setWidth("100%");

        // Efecto visual premium (Bordes curvos y sombra flotante corporativa)
        fotoBeneficios.getStyle()
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.08)");

        colDerecha.add(fotoBeneficios);

        // Ensamblado final
        add(colIzquierda, colDerecha);
    }
}