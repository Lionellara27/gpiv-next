package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoMovimientoRecurso;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Item;
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.model.SolicitudRecurso;
import com.unrn.gpiv.repository.SolicitudRecursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SolicitudRecursoService {

    private final SolicitudRecursoRepository solicitudRepository;
    private final RecursoService recursoService;

    public SolicitudRecursoService(SolicitudRecursoRepository solicitudRepository, RecursoService recursoService) {
        this.solicitudRepository = solicitudRepository;
        this.recursoService = recursoService;
    }

    public List<SolicitudRecurso> listarPendientes() {
        return solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Transactional
    public void crearSolicitud(Empresa empresa, Item item, Integer cantidad, String motivo) throws Exception {
        if (item == null || cantidad == null || cantidad <= 0 || motivo == null || motivo.isBlank()) {
            throw new Exception("Todos los campos son obligatorios y la cantidad debe ser mayor a 0");
        }

        SolicitudRecurso nuevaSolicitud = new SolicitudRecurso();
        nuevaSolicitud.setEmpresa(empresa);
        nuevaSolicitud.setItemSolicitado(item);
        nuevaSolicitud.setCantidad(cantidad);
        nuevaSolicitud.setMotivo(motivo);
        nuevaSolicitud.setEstado(EstadoSolicitud.PENDIENTE);

        solicitudRepository.save(nuevaSolicitud);

        // Aquí se dispararía la notificación interna o log para el Admin
        System.out.println("NOTIFICACIÓN ADMIN -> La empresa " + empresa.getRazonSocial() + " solicitó " + cantidad + "x " + item.getNombre());
    }

    @Transactional
    public void aprobarSolicitud(Long solicitudId) throws Exception {
        SolicitudRecurso solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new Exception("Solicitud no encontrada"));

        List<Recurso> disponibles = recursoService.obtenerTodoElInventario().stream()
                .filter(r -> r.getItem().getId().equals(solicitud.getItemSolicitado().getId()))
                .filter(r -> r.getEstadoMovimiento() == EstadoMovimientoRecurso.DISPONIBLE)
                .toList();

        if (disponibles.size() < solicitud.getCantidad()) {
            throw new Exception("Stock insuficiente en el inventario (" + disponibles.size() + " disponibles).");
        }

        for (int i = 0; i < solicitud.getCantidad(); i++) {
            Recurso recurso = disponibles.get(i);
            recurso.setEstadoMovimiento(EstadoMovimientoRecurso.PRESTADO);
            solicitud.getEmpresa().agregarHerramientaPrestada(recurso);
            recursoService.guardarRecurso(recurso);
        }

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudRepository.save(solicitud);

        // Criterio de aceptación: Comunicar resultado a la empresa
        enviarNotificacionEmail(solicitud.getEmpresa().getRazonSocial(), "APROBADA", null);
    }

    @Transactional
    public void rechazarSolicitud(Long solicitudId, String motivoRechazo) throws Exception {
        SolicitudRecurso solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new Exception("Solicitud no encontrada"));

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitudRepository.save(solicitud);

        enviarNotificacionEmail(solicitud.getEmpresa().getRazonSocial(), "RECHAZADA", motivoRechazo);
    }

    // prueba manual de que se guardaron bien los datos
    private void enviarNotificacionEmail(String empresa, String resultado, String motivo) {
        // ACA DESPUES TENEMOS QUE INYECTAR EL SERVICIO DE GMAIL PARA NOTIFICAR A LAS EMPRESAS (reemplazando los System)
        System.out.println("LOG DE COMUNICACIÓN -> Notificando a " + empresa + ". Su solicitud fue " + resultado);
        if (motivo != null) System.out.println("Motivo de rechazo informado: " + motivo);
    }
}
