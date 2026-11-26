package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoPreferencia;

// Un ingrediente del plato que choca con una preferencia del paciente
public record ConflictoDTO(
        Long alimentoId,
        String alimentoNombre,
        TipoPreferencia tipo,
        SeveridadConflicto severidad
) {}
