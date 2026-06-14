package com.unrn.gpiv.service;

import com.unrn.gpiv.model.HistorialRadicacion;
import com.unrn.gpiv.repository.HistorialRadicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class HistorialService {

    private final HistorialRadicacionRepository repository;

    public HistorialService(HistorialRadicacionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HistorialRadicacion> listarTodo() {
        return repository.findAll();
    }

    @Transactional
    public void registrarAsignacion(String razonSocial, String cuit, String manzana, String nroLote) {
        String nomenclatura = "Mza: " + manzana + " - Lote: " + nroLote;
        HistorialRadicacion nuevo = new HistorialRadicacion(razonSocial, cuit, nomenclatura, java.time.LocalDate.now());
        repository.save(nuevo);
    }

    @Transactional
    public void registrarDesasignacion(String manzana, String nroLote) {
        String nomenclatura = "Mza: " + manzana + " - Lote: " + nroLote;
        repository.findByNomenclaturaLoteAndFechaDesasignacionIsNull(nomenclatura)
                .ifPresent(registro -> {
                    registro.setFechaDesasignacion(java.time.LocalDate.now());
                    repository.save(registro);
                });
    }
}