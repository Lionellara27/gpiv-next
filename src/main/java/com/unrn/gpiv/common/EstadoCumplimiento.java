package com.unrn.gpiv.common;

public enum EstadoCumplimiento {
    TOTAL,      // Cumplió todo el plan de inversión
    PARCIAL,    // Avanzó pero le falta para la meta
    NULO,       // No hizo nada (Lote ocioso)
    INCUMPLIDO  // Hizo algo diferente a lo aprobado
}