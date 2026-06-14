package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.common.EstadoLote;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Estadísticas | GPIV")
@Route(value = "admin/estadisticas", layout = MainLayout.class)
public class AdminEstadisticasView extends VerticalLayout {

    public AdminEstadisticasView(@Autowired EmpresaService empresaService, @Autowired LoteService loteService) {
        setPadding(true);
        add(new H2("Analítica del Parque Industrial"));

        // Traemos los datos frescos
        long libres = loteService.contarLotesPorEstado(EstadoLote.LIBRE);
        long ocupados = loteService.contarLotesPorEstado(EstadoLote.OCUPADO);

        // Agregamos el gráfico
        add(crearGraficoAnilloPiola(libres, ocupados));
    }

    private VerticalLayout crearGraficoAnilloPiola(long libres, long ocupados) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.LARGE);
        card.setWidth("400px"); // Tamaño del contenedor
        card.setAlignItems(Alignment.CENTER);

        // 1. Creamos un Div vacío donde vamos a inyectar un <canvas> de HTML5
        Div canvasContainer = new Div();
        canvasContainer.setWidth("100%");
        canvasContainer.getElement().setProperty("innerHTML", "<canvas></canvas>");

        // 2. Le decimos a Vaadin que importe Chart.js directamente desde internet
        UI.getCurrent().getPage().addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");

        // 3. Ejecutamos el JavaScript puro para dibujar el gráfico con colores modernos
        canvasContainer.getElement().executeJs(
                "const canvas = this.querySelector('canvas');" +
                        "setTimeout(() => {" + // Un pequeño delay para asegurar que Chart.js cargó
                        "  new Chart(canvas, {" +
                        "    type: 'doughnut'," + // 'doughnut' es mucho más moderno que 'pie'
                        "    data: {" +
                        "      labels: ['Lotes Libres', 'Lotes Ocupados']," +
                        "      datasets: [{" +
                        "        data: [$0, $1]," + // Acá pasamos las variables de Java al JS
                        "        backgroundColor: ['#00C853', '#FF3D00']," + // Verde flúor y Naranja/Rojo piola
                        "        hoverOffset: 10," + // Efecto 3D al pasar el mouse
                        "        borderWidth: 0" + // Sin bordes feos
                        "      }]" +
                        "    }," +
                        "    options: {" +
                        "      cutout: '75%'," + // Grosor del anillo
                        "      plugins: {" +
                        "        legend: { position: 'bottom' }" +
                        "      }" +
                        "    }" +
                        "  });" +
                        "}, 500);", (int) libres, (int) ocupados // Estos son los $0 y $1 del JS
        );

        card.add(new H2("Ocupación"), canvasContainer);
        return card;
    }
}

/*SI PAGAS LA LICENCIA FUNCIOAN EPICOOOOOOOOOOOOOOOOOOOOOO
package com.unrn.gpiv.views.admin;

import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.service.LoteService;
import com.unrn.gpiv.views.MainLayout;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@PageTitle("Estadísticas | GPIV")
@Route(value = "admin/estadisticas", layout = MainLayout.class)
public class AdminEstadisticasView extends VerticalLayout {

    private final EmpresaService empresaService;
    private final LoteService loteService;

    public AdminEstadisticasView(@Autowired EmpresaService empresaService, @Autowired LoteService loteService) {
        this.empresaService = empresaService;
        this.loteService = loteService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Analítica del Parque Industrial"));

        HorizontalLayout dashboard = new HorizontalLayout();
        dashboard.setWidthFull();
        dashboard.setSpacing(true);

        // Agregamos los gráficos al layout horizontal (para que queden uno al lado del otro)
        dashboard.add(crearGraficoEmpleo(), crearGraficoLotes());

        add(dashboard);
    }

    // 📊 Gráfico de Barras Verticales (Columnas) para Empleados
    private VerticalLayout crearGraficoEmpleo() {
        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setWidth("50%");

        Map<String, Integer> datos = empresaService.obtenerEmpleadosPorEmpresa();

        // 1. Instanciar el gráfico tipo COLUMNA
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Empleados por Empresa");

        // 2. Configurar el Eje X (Los nombres de las empresas)
        XAxis xAxis = new XAxis();
        xAxis.setCategories(datos.keySet().toArray(new String[0]));
        conf.addxAxis(xAxis);

        // 3. Cargar los datos numéricos
        ListSeries series = new ListSeries("Cantidad de Empleados");
        for (Integer cantidad : datos.values()) {
            series.addData(cantidad);
        }

        conf.addSeries(series);

        contenedor.add(chart);
        return contenedor;
    }

    // 🥧 Gráfico de Torta para Estado de los Lotes
    private VerticalLayout crearGraficoLotes() {
        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setWidth("50%");

        Map<String, Long> datos = loteService.obtenerDistribucionLotes();

        // 1. Instanciar el gráfico tipo TORTA (PIE)
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Distribución de Lotes");

        // 2. Cargar los datos (Nombre de la porción + Valor)
        DataSeries series = new DataSeries();
        series.setName("Lotes");

        datos.forEach((estado, cantidad) -> {
            series.add(new DataSeriesItem(estado, cantidad));
        });

        conf.addSeries(series);

        contenedor.add(chart);
        return contenedor;
    }
}*/