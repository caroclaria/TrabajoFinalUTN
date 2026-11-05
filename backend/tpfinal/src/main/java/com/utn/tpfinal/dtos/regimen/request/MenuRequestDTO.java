package com.utn.tpfinal.dtos.regimen.request;

import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.List;

public record MenuRequestDTO(
        @NotNull (message = "Debe ingresar un numero de lista")
        Integer numeroLista,
        @NotNull (message = "Debe ingresar un tipo de regimen")
        TipoRegimen tipoRegimen,
        @NotNull (message = "Debe ingresar un turno de comida")
        TurnoComida turnoComida,
        @NotNull @NotEmpty (message= "Debe tener al menos un plato")
        List<Plato> platos
) {
}
