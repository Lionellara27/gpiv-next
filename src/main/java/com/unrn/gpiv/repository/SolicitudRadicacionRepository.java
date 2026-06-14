package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.SolicitudRadicacion;
import com.unrn.gpiv.common.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRadicacionRepository extends JpaRepository<SolicitudRadicacion, Long> {


    //NUEVO: Para contar cuántas hay en un estado específico aprobado lo que sea
    long countByEstado(EstadoSolicitud estado);

    //Para que el Admin vea solo lo que tiene que evaluar
    //List<SolicitudRadicacion> findByEstado(EstadoSolicitud estado); //vieja y lenta pero funcional!

    //prueba para traer TODAAAAAAS las solicitudes de 1 tiron! y no 1 a 1 (metodo rapido)
    @Query("SELECT s FROM SolicitudRadicacion s LEFT JOIN FETCH s.proyecto WHERE s.estado = :estado")
    List<SolicitudRadicacion> findByEstado(@Param("estado") EstadoSolicitud estado);

    //Para que un Representante vea sus propias solicitudes enviadas
    List<SolicitudRadicacion> findByRepresentanteId(Long representanteId);

    //Buscar solicitudes por el nombre de la empresa
    List<SolicitudRadicacion> findByRazonSocialPretendidaContainingIgnoreCase(String nombre);
}