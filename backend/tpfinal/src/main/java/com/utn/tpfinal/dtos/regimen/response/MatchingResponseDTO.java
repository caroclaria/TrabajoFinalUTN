package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;

import java.util.List;

// Resultado del motor de matching (RF05): un menú evaluado contra las preferencias de un paciente
public record MatchingResponseDTO(
        Long pacienteId,
        Long menuId,
        Integer numeroLista,
        TipoRegimen tipoRegimen,
        TurnoComida turnoComida,
        boolean apto,                          // true si ningún plato tiene conflictos
        SeveridadConflicto severidad,          // la más grave de todo el menú
        List<PlatoEvaluadoDTO> platos
) {}
