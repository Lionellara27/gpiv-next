package com.unrn.gpiv.service;

import com.unrn.gpiv.model.InformeAvance;
import com.unrn.gpiv.repository.InformeAvanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InformeAvanceService {
    @Autowired
    private InformeAvanceRepository informeRepository;

    public List<InformeAvance> obtenerInformesMesActual() {
        // Obtenemos el primer día del mes actual (ej: 2026-06-01)
        LocalDate inicioMes = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        // Obtenemos el último día del mes actual (ej: 2026-06-30)
        LocalDate finMes = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        // ¡Ahora sí! Completamos la consulta con las fechas reales
        return informeRepository.findByFechaEvaluacionBetween(inicioMes, finMes);
    }
}