package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.RepresentanteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepresentanteEmpresaRepository extends JpaRepository<RepresentanteEmpresa, Long> {
    // BÚSQUEDAS (Para el Login y Sesión)
    // Buscamos por username (que en tu caso es el email)
    Optional<RepresentanteEmpresa> findByUsername(String username);

    // Por las dudas, si querés buscar específicamente por el campo email
    Optional<RepresentanteEmpresa> findByEmail(String email);


    //VALIDACIONES
    boolean existsByUsername(String username);
    boolean existsByDni(String dni);
    boolean existsByCuitPersonal(String cuitPersonal);
}