package com.unrn.gpiv.model;

import com.unrn.gpiv.common.Rol;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN") // Para que se guarde en la tabla 'usuarios' como un ADMIN
public class Administrador extends Usuario {

    // NO lleva ID, usa el del padre (Usuario)

    private String nombreCompleto;

    private String oficina; // Ejemplo: "Edificio Central - Planta Alta"

    public Administrador() {
        // Le asignamos automáticamente el permiso de ADMIN al nacer
        this.setRol(Rol.ADMIN);
    }

    // --- GETTERS Y SETTERS ---

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getOficina() {
        return oficina;
    }

    public void setOficina(String oficina) {
        this.oficina = oficina;
    }
}
