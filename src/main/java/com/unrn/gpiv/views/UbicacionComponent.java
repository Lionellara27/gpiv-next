package com.unrn.gpiv.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.IFrame; // 🎯 EL COMPONENTE MÁGICO PARA EL MAPA NATIVO
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class UbicacionComponent extends HorizontalLayout {

    public UbicacionComponent() {
        // Configuración de la sección partida a la mitad (Split Layout)
        setWidthFull();
        setSpacing(false);
        getStyle().set("padding", "60px 80px");
        getStyle().set("font-family", "'Montserrat', sans-serif");
        getStyle().set("background-color", "#FFFFFF");
        setAlignItems(Alignment.START);

        // COLUMNA IZQUIERDA: INDICACIONES Y DISTANCIAS
        VerticalLayout colIzquierda = new VerticalLayout();
        colIzquierda.setWidth("45%"); // Ocupa casi la mitad izquierda
        colIzquierda.setPadding(false);
        colIzquierda.setSpacing(true);

        H2 titleUbicacion = new H2("Ubicación Estratégica");
        titleUbicacion.getStyle().set("color", "#0063BE").set("font-weight", "700").set("margin", "0 0 15px 0");

        Paragraph descUbicacion = new Paragraph("Su ubicación es uno de sus principales atributos con respecto a otros complejos industriales de la región. Se encuentra estratégicamente conectado mediante la Ruta Nacional 3, facilitando el ingreso y egreso de transporte de carga pesada de forma directa, sin necesidad de transitar por el casco céntrico residencial de la ciudad de Viedma.");
        descUbicacion.getStyle().set("color", "#2D3748").set("font-size", "1.1em").set("line-height", "1.6");

        Paragraph descLogistica = new Paragraph("Cuenta con una localización privilegiada como punto de convergencia logística en el portal de acceso a la Patagonia Atlántica. Esto permitirá a las empresas radicadas consolidar cadenas de valor tanto a nivel regional como internacional.");
        descLogistica.getStyle().set("color", "#4A5568").set("font-size", "1.05em").set("line-height", "1.6");

        // Sub-bloque de distancias clavadas de Viedma
        VerticalLayout bloqueDistancias = new VerticalLayout();
        bloqueDistancias.setPadding(false);
        bloqueDistancias.getStyle().set("margin-top", "25px");

        H3 titleDistancias = new H3("📍 Distancias de Conectividad:");
        titleDistancias.getStyle().set("color", "#1A202C").set("font-weight", "700").set("margin-bottom", "15px");
        bloqueDistancias.add(titleDistancias);

        String[] distanciasViedma = {
                "• Centro de la Ciudad de Viedma: 4 km.",
                "• Aeropuerto Gobernador Edgardo Castello: 6 km.",
                "• Empalme Ruta Provincial 1 (Camino al Cóndor): 2 km.",
                "• Río Negro / Zona Costanera: 3 km.",
                "• Puerto de San Antonio Este (SAE): 180 km.",
                "• Conexión Directa con Carmen de Patagones: 6 km."
        };

        for (String item : distanciasViedma) {
            Span line = new Span(item);
            line.getStyle()
                    .set("color", "#2D3748")
                    .set("font-weight", "600")
                    .set("font-size", "1.05em")
                    .set("margin-bottom", "8px");
            bloqueDistancias.add(line);
        }

        colIzquierda.add(titleUbicacion, descUbicacion, descLogistica, bloqueDistancias);


        // COLUMNA DERECHA: EL MAPA INTERACTIVO (GOOGLE MAPS EMBED)
        VerticalLayout colDerecha = new VerticalLayout();
        colDerecha.setWidth("55%"); // Un poquito más ancha para resaltar el mapa
        colDerecha.setPadding(false);
        getStyle().set("margin-left", "40px");

        // Creamos el Iframe apuntando al mapa satelital del Parque Industrial de Viedma
        IFrame mapa = new IFrame();
        mapa.setSrc("https://maps.google.com/maps?q=-40.8252,-62.9645&t=k&z=15&ie=UTF8&iwloc=&output=embed");
        mapa.setWidth("100%");
        mapa.setHeight("500px"); // Alto clavado para que se luzca

        // Estilizado premium de la tarjeta del mapa (bordes redondeados y sombra suave)
        mapa.getStyle()
                .set("border", "1px solid #E2E8F0")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.05)");

        colDerecha.add(mapa);

        // Agregamos las dos columnas principales al layout horizontal dividido
        add(colIzquierda, colDerecha);
    }
}