package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Devuelve true si el email ya existe, false si está libre.
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // PARA EL LOGIN O RECUPERACIÓN
    // Busca al usuario por email (usamos Optional para evitar NullPointerException)
    Optional<Usuario> findByEmail(String email);

    // Este es el método que va a buscar por email/username en toda la tabla
    Optional<Usuario> findByUsername(String username);
}