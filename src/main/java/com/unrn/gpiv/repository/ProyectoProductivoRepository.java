package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.ProyectoProductivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoProductivoRepository extends JpaRepository<ProyectoProductivo, Long> {
    // ¡Vacío! Spring Boot ya sabe cómo guardar, borrar y buscar proyectos gracias a JpaRepository.
}
