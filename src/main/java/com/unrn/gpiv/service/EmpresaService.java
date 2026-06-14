package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.common.EstadoSolicitud;
import com.unrn.gpiv.model.*;
import com.unrn.gpiv.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private SolicitudRadicacionRepository solicitudRepository;
    @Autowired
    private RepresentanteEmpresaRepository representanteRepository;
    @Autowired
    private ProyectoProductivoRepository proyectoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
//parte para contadores

    // Tu EmpresaService debería quedar solo con esto:

    @Transactional
    public RepresentanteEmpresa registrarRepresentante(RepresentanteEmpresa rep) {
        if (usuarioRepository.existsByEmail(rep.getEmail())) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado. Por favor, iniciá sesión.");
        }

        if (usuarioRepository.existsByUsername(rep.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        if (representanteRepository.existsByDni(rep.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con este DNI.");
        }

        if (representanteRepository.existsByCuitPersonal(rep.getCuitPersonal())) {
            throw new IllegalArgumentException("Este CUIT ya está vinculado a otra cuenta.");
        }

        try {
            return representanteRepository.save(rep);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de base de datos: Algunos de los datos ya existen.");
        }
    }

    @Transactional
    public void recibirSolicitud(ProyectoProductivo proyecto, RepresentanteEmpresa rep, String razonSocial) {
        if (proyecto.getPdfProyecto() == null || proyecto.getPdfProyecto().length == 0) {
            throw new IllegalArgumentException("El archivo PDF del proyecto es obligatorio.");
        }

        ProyectoProductivo proyectoGuardado = proyectoRepository.save(proyecto);

        SolicitudRadicacion solicitud = new SolicitudRadicacion();
        solicitud.setProyecto(proyectoGuardado);
        solicitud.setRepresentante(rep);
        solicitud.setRazonSocialPretendida(razonSocial);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        solicitudRepository.save(solicitud);
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> obtenerPorId(Long id) {
        return empresaRepository.findById(id);
    }

    //agregué este metodo para terminar de completar la empresa (no funcionaba bien antes)
    @Transactional
    public void completarRegistroLegalEmpresa(Long empresaId, String direccion, String tipoSociedad, String telEmergencia, String inscripcion) {
        //Buscamos el registro directamente de la base de datos dentro de la transacción
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("No se encontró la empresa para completar el registro legal."));

        //Setea solo los campos nuevos del formulario legal
        empresa.setDireccion(direccion);
        empresa.setTipoSociedad(tipoSociedad);
        empresa.setTelefonoEmergencia(telEmergencia);
        empresa.setInscripcionRegistral(inscripcion);

        // Forzamos que los estados estén firmes
        empresa.setEstado(EstadoSolicitud.APROBADA);
        empresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        if (empresa.getFechaRadicacion() == null) {
            empresa.setFechaRadicacion(LocalDate.now());
        }
        empresaRepository.save(empresa);
    }

    //PRE-APROBAR FASE 1 (Pasa a PRE_APROBADO)
    @Transactional
    public void preAprobarFase1(Long solicitudId) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + solicitudId));

        solicitud.setEstado(EstadoSolicitud.PRE_APROBADO);
        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void enviarDocumentacionFase2(Long solicitudId,
                                         byte[] doc1, String nombre1,
                                         byte[] doc2, String nombre2,
                                         byte[] doc3, String nombre3) {

        SolicitudRadicacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        ProyectoProductivo proyecto = solicitud.getProyecto();

        proyecto.setAdjuntoFase2_1(doc1);
        proyecto.setNombreAdjunto1(nombre1);

        proyecto.setAdjuntoFase2_2(doc2);
        proyecto.setNombreAdjunto2(nombre2);

        proyecto.setAdjuntoFase2_3(doc3);
        proyecto.setNombreAdjunto3(nombre3);

        proyectoRepository.save(proyecto);

        solicitud.setEstado(EstadoSolicitud.DOCUMENTACION_ENVIADA);
        solicitudRepository.save(solicitud);
    }

    //APROBACIÓN RADICACIÓN FINAL (Fase 2 completa)
    @Transactional
    public Empresa aprobarRadicacionFinal(Long solicitudId, byte[] archivoActa, String nombreArchivoActa) {
        if (archivoActa == null || archivoActa.length == 0) {
            throw new IllegalArgumentException("El archivo PDF del Acta de Radicación es obligatorio.");
        }

        SolicitudRadicacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + solicitudId));

        //Instanciamos la empresa física que operará en el parque
        Empresa nuevaEmpresa = new Empresa();
        nuevaEmpresa.setRazonSocial(solicitud.getRazonSocialPretendida());
        nuevaEmpresa.setRepresentante(solicitud.getRepresentante());
        nuevaEmpresa.setProyecto(solicitud.getProyecto());
        nuevaEmpresa.setTitulada(false);
        nuevaEmpresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        nuevaEmpresa.setFechaRadicacion(LocalDate.now());

        //Guardamos el acta de radicacion
        nuevaEmpresa.setPdfActaRadicacion(archivoActa);
        nuevaEmpresa.setNombreActaRadicacion(nombreArchivoActa);

        if (solicitud.getRepresentante() != null && solicitud.getRepresentante().getCuitPersonal() != null) {
            nuevaEmpresa.setCuit(solicitud.getRepresentante().getCuitPersonal());
        } else {
            nuevaEmpresa.setCuit("A DEFINIR");
        }

        //Cerramos la solicitud formal del administrador
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitudRepository.save(solicitud);


        return empresaRepository.save(nuevaEmpresa);
    }

    //DESADJUDICAR EMPRESA
    @Transactional
    public void desadjudicarEmpresa(Long empresaId, byte[] archivoActa, String nombreArchivoActa) {
        if (archivoActa == null || archivoActa.length == 0) {
            throw new IllegalArgumentException("El archivo PDF del Acta de Desadjudicación es obligatorio.");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("No se encontró la empresa con ID: " + empresaId));

        //Cambiamos el estado operativo de la empresa
        empresa.setEstadoEmpresa(EstadoEmpresa.DESADJUDICADA);

        //Guardamos el acta de desadjudicación
        empresa.setPdfActaDesadjudicacion(archivoActa);
        empresa.setNombreActaDesadjudicacion(nombreArchivoActa);

        empresaRepository.save(empresa);
    }

    @Transactional
    public void actualizarEmpresa(Empresa empresa) {
        empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa aprobarRadicacion(Long solicitudId) {
        SolicitudRadicacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Empresa nuevaEmpresa = new Empresa();
        nuevaEmpresa.setRazonSocial(solicitud.getRazonSocialPretendida());
        nuevaEmpresa.setRepresentante(solicitud.getRepresentante());
        nuevaEmpresa.setProyecto(solicitud.getProyecto());
        nuevaEmpresa.setTitulada(false);
        nuevaEmpresa.setEstado(EstadoSolicitud.APROBADA);
        nuevaEmpresa.setEstadoEmpresa(EstadoEmpresa.RADICADA);
        nuevaEmpresa.setFechaRadicacion(LocalDate.now());

        if (solicitud.getRepresentante() != null && solicitud.getRepresentante().getCuitPersonal() != null) {
            nuevaEmpresa.setCuit(solicitud.getRepresentante().getCuitPersonal());
        } else {
            nuevaEmpresa.setCuit("A DEFINIR");
        }

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        return empresaRepository.save(nuevaEmpresa);
    }

    @Transactional
    public void titularEmpresa(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("No se encontró la empresa con ID: " + empresaId));

        empresa.setTitulada(true);
        empresa.setEstadoEmpresa(EstadoEmpresa.TITULADA);

        empresaRepository.save(empresa);
    }

    public Usuario loginGeneral(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }

    @Transactional(readOnly = true)
    public Empresa obtenerEmpresaPorRepresentante(RepresentanteEmpresa rep) {
        if (rep == null || rep.getId() == null) return null;

        // Buscamos la empresa vinculada al representante
        Empresa empresa = empresaRepository.findByRepresentante(rep).orElse(null);

        if (empresa != null) {
            // Inicializamos las colecciones LAZY para evitar LazyInitializationException
            if (empresa.getProyecto() != null) {
                org.hibernate.Hibernate.initialize(empresa.getProyecto());
                if (empresa.getProyecto().getServiciosNecesarios() != null) {
                    org.hibernate.Hibernate.initialize(empresa.getProyecto().getServiciosNecesarios());
                }
            }
            org.hibernate.Hibernate.initialize(empresa.getLotesAsignados());
            org.hibernate.Hibernate.initialize(empresa.getEmpleados());
            org.hibernate.Hibernate.initialize(empresa.getVehiculos());
            org.hibernate.Hibernate.initialize(empresa.getConsumosMensuales());
            org.hibernate.Hibernate.initialize(empresa.getInformesDeAvance());

            //nuevo para ver lo prestado
            org.hibernate.Hibernate.initialize(empresa.getRecursosAsignados());
        }
        return empresa;
    }

    public RepresentanteEmpresa login(String email, String password) {
        return representanteRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Usuario o password inválidos"));
    }

    @Transactional(readOnly = true)
    public SolicitudRadicacion obtenerUltimaSolicitud(RepresentanteEmpresa rep) {
        List<SolicitudRadicacion> solicitudes = solicitudRepository.findByRepresentanteId(rep.getId());

        if (solicitudes.isEmpty()) {
            return null;
        }

        return solicitudes.get(solicitudes.size() - 1);
    }

    public SolicitudRadicacion obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontré la solicitud con ID: " + id));
    }

    @Transactional
    public void actualizarSolicitud(SolicitudRadicacion solicitud) {
        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void marcarComoEnEvaluacion(Long solicitudId) {
        SolicitudRadicacion sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("No se encontró la solicitud"));

        if (sol.getEstado() == EstadoSolicitud.PENDIENTE) {
            sol.setEstado(EstadoSolicitud.EN_EVALUACION);
            solicitudRepository.save(sol);
        }
    }

    //parte de conteo de solicitud, lotes, inventario, etc
    public long contarSolicitudesPendientes() {
        return solicitudRepository.countByEstado(EstadoSolicitud.PENDIENTE);
    }

    public long contarEmpresasRadicadas() {
        return empresaRepository.countByEstadoEmpresa(EstadoEmpresa.RADICADA)+ + empresaRepository.countByEstadoEmpresa(EstadoEmpresa.TITULADA);
    }

    //________________

    @Transactional(readOnly = true)
    public List<Empresa> listarTodasLasAprobadas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> obtenerEmpresaCompletaPorId(Long id) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);

        empresaOpt.ifPresent(empresa -> {
            if (empresa.getProyecto() != null) {
                org.hibernate.Hibernate.initialize(empresa.getProyecto());
            }
            org.hibernate.Hibernate.initialize(empresa.getLotesAsignados());
            org.hibernate.Hibernate.initialize(empresa.getInformesDeAvance());
            org.hibernate.Hibernate.initialize(empresa.getHerramientasAportadas());
            org.hibernate.Hibernate.initialize(empresa.getHerramientasPrestadas());

            empresa.getHerramientasAportadas().forEach(r -> {
                if (r.getItem() != null) org.hibernate.Hibernate.initialize(r.getItem());
            });
            empresa.getHerramientasPrestadas().forEach(r -> {
                if (r.getItem() != null) org.hibernate.Hibernate.initialize(r.getItem());
            });
        });

        return empresaOpt;
    }

    //parte estadistica
    // Agregá esto para contar empleados por empresa (Top 5)
    @Transactional(readOnly = true)
    public Map<String, Integer> obtenerEmpleadosPorEmpresa() {
        return empresaRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Empresa::getRazonSocial,
                        e -> e.getEmpleados().size()
                ));
    }
}