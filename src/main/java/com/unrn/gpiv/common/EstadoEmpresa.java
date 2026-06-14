package com.unrn.gpiv.common;

public enum EstadoEmpresa {
    INTERESADA, // Todavía no tiene lote, está en proceso de evaluación
    RADICADA,   // Ya tiene un lote asignado y está trabajando
    TITULADA,    // Ya cumplió todo el proyecto y obtuvo la escritura (HU 11)
    DESADJUDICADA
}