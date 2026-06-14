package com.unrn.gpiv.common;

public enum EstadoSolicitud {
    PENDIENTE, EN_EVALUACION,

    PRE_APROBADO,//cuando pasó la fase 1. Esperando documentacion de fase 2
    DOCUMENTACION_ENVIADA,//la empresa subió los archivos, admin debe evaluar final
    APROBADA, //aprobacion final, crea la empresa y habilita carga final de datos (de la empresa)
    RECHAZADA
}
