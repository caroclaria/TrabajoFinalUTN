package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MenuResponseDTO(
        Integer numeroLista,
        TipoRegimen tipoRegimen,
        TurnoComida turnoComida,
        List<Plato> platos
) {
}
