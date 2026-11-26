package com.utn.tpfinal.enums;

// Los valores coinciden con el CHECK de la columna preferencia_alimentaria.tipo
public enum TipoPreferencia {
    ACEPTADO,
    RECHAZO,        // aversión: el paciente no lo come, pero no es peligroso
    ALERGIA,
    INTOLERANCIA
}
