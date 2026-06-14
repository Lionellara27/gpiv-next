package com.unrn.gpiv.repository;
import com.unrn.gpiv.model.SolicitudRecurso;
import com.unrn.gpiv.common.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRecursoRepository extends JpaRepository<SolicitudRecurso, Long> {
    List<SolicitudRecurso> findByEstado(EstadoSolicitud estado);
}
