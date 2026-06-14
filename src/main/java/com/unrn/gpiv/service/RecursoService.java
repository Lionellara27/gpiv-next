package com.unrn.gpiv.service;

import com.unrn.gpiv.common.EstadoMovimientoRecurso; // 🟢 Tus enums fuertes
import com.unrn.gpiv.model.Recurso;
import com.unrn.gpiv.repository.RecursoRepository;
import com.unrn.gpiv.model.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true) //
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoService(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    // --- MÉTODOS DE LECTURA (Buscadores para la Grilla) ---

    public List<Recurso> obtenerTodoElInventario() {
        return recursoRepository.findAll();
    }

    public List<Recurso> obtenerRecursosDisponibles() {
        return recursoRepository.findByEstadoMovimiento(EstadoMovimientoRecurso.DISPONIBLE);
    }

    public List<Recurso> buscarPorCategoria(String categoria) {
        // Llama a la consulta inter-tablas mágica que busca la categoría dentro de Item
        return recursoRepository.findByItemCategoriaContainingIgnoreCase(categoria);
    }

    // --- 📊 MÉTODOS PARA EL DASHBOARD (Números 100% Reales) ---

    public long obtenerCantidadPrestados() {
        return recursoRepository.countByEstadoMovimiento(EstadoMovimientoRecurso.PRESTADO) + recursoRepository.countByEstadoMovimiento(EstadoMovimientoRecurso.A_DEVOLVER);
    }

    public long obtenerCantidadADevolver() {
        return recursoRepository.countByEstadoMovimiento(EstadoMovimientoRecurso.A_DEVOLVER);
    }

    // --- MÉTODOS DE ESCRITURA (Lógica transaccional de control) ---

    // 🎯 EL MÉTODO QUE FALTABA: Guarda las altas nuevas que vienen desde el Modal de Vaadin
    @Transactional
    public void guardarRecurso(Recurso recurso) {
        recursoRepository.save(recurso);
    }

    @Transactional
    public void prestarRecurso(Long recursoId, Empresa empresa) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado en el inventario"));

        // Comprobación segura usando el Enum
        if (recurso.getEstadoMovimiento() != EstadoMovimientoRecurso.DISPONIBLE) {
            throw new RuntimeException("El recurso no está libre. Estado actual: " + recurso.getEstadoMovimiento());
        }

        recurso.setPrestadoA(empresa);
        recurso.setEstadoMovimiento(EstadoMovimientoRecurso.PRESTADO); // Mutación de estado pro
        recursoRepository.save(recurso);
    }

    @Transactional
    public void devolverRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Limpiamos la asignación y lo volvemos a dejar en el pañol central
        recurso.setPrestadoA(null);
        recurso.setEstadoMovimiento(EstadoMovimientoRecurso.DISPONIBLE);
        recursoRepository.save(recurso);
    }

    @Transactional
    public void marcarParaDevolucion(Long recursoId) {
        // Estado intermedio útil antes de que el administrativo firme el reingreso físico
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        recurso.setEstadoMovimiento(EstadoMovimientoRecurso.A_DEVOLVER);
        recursoRepository.save(recurso);
    }

    @Transactional
    public void darDeBaja(Long id) {
        // HU 19: Eliminación definitiva por rotura, desuso o pérdida total
        if (!recursoRepository.existsById(id)) {
            throw new RuntimeException("El recurso que intenta eliminar no existe");
        }
        recursoRepository.deleteById(id);
    }

    public List<Recurso> listarRecursosPrestadosA(Empresa empresa) {
        return recursoRepository.findByPrestadoAAndEstadoMovimiento(empresa, EstadoMovimientoRecurso.PRESTADO);
    }

}