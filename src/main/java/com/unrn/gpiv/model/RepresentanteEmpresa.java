package com.unrn.gpiv.model;

import com.unrn.gpiv.common.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("REPRESENTANTE")
@Getter @Setter // 🟢 Lombok genera automáticamente todos los getters y setters por atrás
public class RepresentanteEmpresa extends Usuario {

    // Datos Personales del Humano que maneja la cuenta
    private String nombreCompleto;
    private String dni;
    private String cuitPersonal;
    private String telefono;
    private String emailContacto;

    // 🎯 NUEVOS: Los campos obligatorios que el tipo carga en el SignUp original
    private String razonSocialInicial;
    private String cuitEmpresaInicial;

    // Relación bidireccional con la firma real
    @OneToOne(mappedBy = "representante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Empresa empresa;

    // El constructor fuerza a que este tipo de usuario tenga rol de empresa
    public RepresentanteEmpresa() {
        this.setRol(Rol.EMPRESA);
    }

    // GETTERS Y SETTERS
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombre) { this.nombreCompleto = nombre; }

}

//debo probar si funciona!
//package com.unrn.gpiv.model;
//
//import com.unrn.gpiv.common.Rol; // Importamos tu Enum
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Entity
//@DiscriminatorValue("REPRESENTANTE")
//@Getter @Setter
//public class RepresentanteEmpresa extends Usuario {
//
//    private String nombreCompleto;
//    private String dni;
//    private String cuitPersonal;
//    private String telefono;
//    private String emailContacto;
//
//    @OneToOne(mappedBy = "representante")
//    private Empresa empresa;
//
//    // EL CONSTRUCTOR CORREGIDO:
//    public RepresentanteEmpresa() {
//        this.setRol(Rol.EMPRESA);
//    }
//
//    // --- GETTERS Y SETTERS ---
//    public String getNombreCompleto() { return nombreCompleto; }
//    public void setNombreCompleto(String nombre) { this.nombreCompleto = nombre; }
//    public String getDni() { return dni; }
//    public void setDni(String dni) { this.dni = dni; }
//    public String getCuitPersonal() { return cuitPersonal; }
//    public void setCuitPersonal(String cuit) { this.cuitPersonal = cuit; }
//    public String getTelefono() { return telefono; }
//    public void setTelefono(String tel) { this.telefono = tel; }
//    public String getEmailContacto() { return emailContacto; }
//    public void setEmailContacto(String email) { this.emailContacto = email; }
//    public Empresa getEmpresa() { return empresa; }
//    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
//}