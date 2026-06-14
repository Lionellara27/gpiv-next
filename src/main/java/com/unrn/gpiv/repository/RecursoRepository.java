package com.unrn.gpiv.repository;

import com.unrn.gpiv.common.EstadoMovimientoRecurso; // 🟢 Importamos tu Enum fuerte
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // Trae la lista de recursos según su estado de movimiento (Ej: DISPONIBLE)
    List<Recurso> findByEstadoMovimiento(EstadoMovimientoRecurso estadoMovimiento);
    List<Recurso> findByPrestadoAAndEstadoMovimiento(Empresa empresa, EstadoMovimientoRecurso estado);

    // Spring viaja a través de la relación 'item' y filtra por su campo 'categoria' ignorando mayúsculas.
    List<Recurso> findByItemCategoriaContainingIgnoreCase(String categoria);

    // Cuenta cuántos registros físicos hay en la base de datos
    long countByEstadoMovimiento(EstadoMovimientoRecurso estadoMovimiento);
}