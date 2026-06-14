package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.InformeAvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InformeAvanceRepository extends JpaRepository<InformeAvance, Long> {
    // Si necesitas traer informes de una empresa específica:
    // SELECT * FROM informes_avance WHERE fecha_evaluacion BETWEEN ?1 AND ?2
    List<InformeAvance> findByFechaEvaluacionBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
