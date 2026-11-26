package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.SeveridadConflicto;

import java.util.List;

// Resultado del matching para un plato del menú
public record PlatoEvaluadoDTO(
        Long platoId,
        String nombre,
        boolean apto,
        SeveridadConflicto severidad,          // la más grave de sus conflictos
        List<ConflictoDTO> conflictos,
        List<PlatoReemplazoDTO> reemplazos     // vacía si el plato es apto
) {}
