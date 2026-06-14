package com.unrn.gpiv.views;

import com.unrn.gpiv.common.Rol;
import com.unrn.gpiv.common.EstadoSolicitud;
//import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.model.Usuario;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import com.unrn.gpiv.service.EmpresaService;
import com.unrn.gpiv.views.admin.*;
import com.unrn.gpiv.views.empresa.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private final EmpresaService empresaService;

    @Autowired
    public MainLayout(EmpresaService empresaService) {
        this.empresaService = empresaService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("SGPIV - Viedma");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM, LumoUtility.TextColor.PRIMARY);

        DrawerToggle toggle = new DrawerToggle();

        Button btnLogout = new Button("Salir", VaadinIcon.SIGN_OUT.create(), e -> {
            VaadinSession.getCurrent().setAttribute("usuarioLogueado", null);
            VaadinSession.getCurrent().close();
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().setLocation("login");
        });

        btnLogout.addClassNames(LumoUtility.Margin.Left.AUTO, LumoUtility.Margin.Right.MEDIUM);

        var header = new HorizontalLayout(toggle, logo, btnLogout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        Usuario usuario = (Usuario) VaadinSession.getCurrent().getAttribute("usuarioLogueado");

        if (usuario == null) return;

        // =====================================================================
        // 🛡️ SECCIÓN 1: ROL ADMINISTRADOR (ENREPAVI)
        // =====================================================================
        if (usuario.getRol() == Rol.ADMIN) {
            Span adminSection = new Span("ADMINISTRACIÓN");
            adminSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);

            menu.add(adminSection);
            menu.add(crearLink(AdminDashboardView.class, VaadinIcon.CHART_LINE, " Dashboard"));
            menu.add(crearLink(AdminLotesView.class, VaadinIcon.MAP_MARKER, " Gestión de Lotes"));
            menu.add(crearLink(InformesEmpresasView.class, VaadinIcon.CHART_3D, " Informe de Empresas"));
            menu.add(crearLink(EvaluarSolicitudesView.class, VaadinIcon.CHECK_SQUARE_O, " Evaluar Solicitudes"));
            menu.add(crearLink(InventarioView.class, VaadinIcon.TOOLS, " Gestión de Inventario"));
            menu.add(crearLink(SolicitudesRecursosView.class, VaadinIcon.CLIPBOARD_TEXT, " Solicitudes de Recursos"));
            menu.add(crearLink(HistorialDashboardView.class, VaadinIcon.TIME_BACKWARD, " Historiales y Auditoría"));
            menu.add(crearLink(AdminEstadisticasView.class, VaadinIcon.PIE_CHART, " Estadísticas"));
        }

        // =====================================================================
        // 🏢 SECCIÓN 2: ROL EMPRESA (MENÚ DINÁMICO REACTIVO)
        // =====================================================================
        if (usuario.getRol() == Rol.EMPRESA && usuario instanceof RepresentanteEmpresa logueado) {

            Span empresaSection = new Span("ADMINISTRACIÓN");
            empresaSection.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);
            menu.add(empresaSection);

            menu.add(crearLink(MiProyectoView.class, VaadinIcon.FACTORY, " Mi Proyecto"));

            SolicitudRadicacion solicitud = empresaService.obtenerUltimaSolicitud(logueado);

            if (solicitud != null) {
                if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
                    menu.add(crearLink(MiEmpresaView.class, VaadinIcon.BUILDING, " Mi Empresa"));
                }

                // 🚀 LA MAGIA PARA EL F5: Le pedimos a la BD la empresa actualizada SIEMPRE
                Empresa empresaFresca = empresaService.obtenerEmpresaPorRepresentante(logueado);

                // Verificamos si la empresa fresca ya tiene lotes
                if (empresaFresca != null && !empresaFresca.getLotesAsignados().isEmpty()) {

                    // UN SOLO SPAN (Para que no salga duplicado)
                    Span tituloAvance = new Span("OPERACIONES INDUSTRIALES");
                    tituloAvance.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Top.MEDIUM);
                    menu.add(tituloAvance);

                    // Nuestro botón unificado que lleva a las 3 pestañas
                    menu.add(crearLink(AvancesObraView.class, VaadinIcon.CHART_TIMELINE, " Mi Avance"));
                    // ACCESO EXCLUSIVO A LAS EMPRESAS RADICADAS
                    menu.add(crearLink(SolicitarRecursoView.class, VaadinIcon.CUBES, " Herramientas y Recursos"));
                }
            }
        }
        // 🟢 ARREGLADO: El menú se agrega al contenedor lateral y cerramos el método limpiamente
        addToDrawer(menu);
    }

    // 🟢 ARREGLADO: El método auxiliar ahora queda perfectamente aislado a nivel de clase
    private RouterLink crearLink(Class viewClass, VaadinIcon icon, String text) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.add(icon.create(), new Span(text));
        link.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
        return link;
    }
}