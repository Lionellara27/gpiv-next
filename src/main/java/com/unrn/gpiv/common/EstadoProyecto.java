package com.unrn.gpiv.common;

public enum EstadoProyecto {
    PROPUESTO,      // Recién cargado por la empresa
    EN_EVALUACION,  // Siendo revisado por el Directorio
    APROBADO,       // Proyecto validado
    RECHAZADO,      // No cumple los requisitos
    ACTIVO,         // En etapa de construcción o producción
    FINALIZADO      // Obra terminada al 100%
}