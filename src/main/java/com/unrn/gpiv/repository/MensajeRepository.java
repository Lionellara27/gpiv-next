package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.Mensaje;
import com.unrn.gpiv.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // Método para buscar mensajes que recibió un usuario y no leyó
    List<Mensaje> findByReceptorAndLeidoFalse(Usuario receptor);

    // Método para ver el historial de charla entre dos personas
    List<Mensaje> findByEmisorOrReceptorOrderByFechaEnvioDesc(Usuario emisor, Usuario receptor);
}