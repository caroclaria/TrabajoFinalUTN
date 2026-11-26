package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;

import java.util.List;

public record MenuResponseDTO(
        Long id,
        Integer numeroLista,
        TipoRegimen tipoRegimen,
        TurnoComida turnoComida,
        List<PlatoResponseDTO> platos
) {
}
