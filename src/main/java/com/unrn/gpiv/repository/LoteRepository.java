package com.unrn.gpiv.repository;

import com.unrn.gpiv.common.EstadoLote;
import org.springframework.stereotype.Repository;
import com.unrn.gpiv.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    //permite buscar, guardar o borrar datos sin escribir SQL manualmente.

    List<Lote> findByEstado(EstadoLote estado);

    //tmb para contar
    long countByEstado(EstadoLote estado);

}
