package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoPreferencia;

public record PreferenciaResponseDTO(
        Long id,
        Long pacienteId,
        Long alimentoId,
        String alimentoNombre,
        TipoPreferencia tipo,
        SeveridadConflicto severidad   // para pintar el color en el frontend
) {}
