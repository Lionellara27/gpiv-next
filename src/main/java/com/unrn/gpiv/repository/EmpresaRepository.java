package com.unrn.gpiv.repository;

import com.unrn.gpiv.common.EstadoEmpresa;
import com.unrn.gpiv.model.Empresa;
import com.unrn.gpiv.model.RepresentanteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    //para valiar el usuario y mostrar luego als vistas
    Optional<Empresa> findByRepresentante(RepresentanteEmpresa representante);

    // Para buscar por CUIT (cuando ya esta radicada)
    Optional<Empresa> findByCuit(String cuit);

    // Para validar si una Razon Social ya existe y evitar duplicados
    boolean existsByRazonSocial(String razonSocial);
//para contar
    long countByEstadoEmpresa(EstadoEmpresa estado);

}