package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.HistorialRadicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialRadicacionRepository extends JpaRepository<HistorialRadicacion, Long> {
    // Busca el registro que siga activo (sin fecha de salida) para un lote específico
    Optional<HistorialRadicacion> findByNomenclaturaLoteAndFechaDesasignacionIsNull(String nomenclatura);
}
