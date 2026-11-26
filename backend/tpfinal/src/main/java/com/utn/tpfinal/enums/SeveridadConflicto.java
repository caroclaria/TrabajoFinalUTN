package com.utn.tpfinal.enums;

// Qué tan grave es que un plato contenga un alimento en conflicto con el paciente.
// El frontend la usa para el código de colores (RNF03): CRITICA = rojo, ALTA = naranja, BAJA = amarillo.
public enum SeveridadConflicto {
    NINGUNA,
    BAJA,       // RECHAZO: bandeja que probablemente vuelva sin consumir
    ALTA,       // INTOLERANCIA: malestar digestivo
    CRITICA;    // ALERGIA: riesgo para la salud del paciente

    public static SeveridadConflicto desde(TipoPreferencia tipo) {
        return switch (tipo) {
            case ALERGIA -> CRITICA;
            case INTOLERANCIA -> ALTA;
            case RECHAZO -> BAJA;
            case ACEPTADO -> NINGUNA;
        };
    }
}
