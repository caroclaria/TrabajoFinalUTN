package com.utn.tpfinal.dtos.regimen.request;

import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// El cliente manda solo los IDs de los platos; el service los busca en la base.
public record MenuRequestDTO(
        @NotNull(message = "Debe ingresar un número de lista")
        @Min(value = 1, message = "El número de lista va de 1 a 10")
        @Max(value = 10, message = "El número de lista va de 1 a 10")
        Integer numeroLista,

        @NotNull(message = "Debe ingresar un tipo de régimen")
        TipoRegimen tipoRegimen,

        @NotNull(message = "Debe ingresar un turno de comida")
        TurnoComida turnoComida,

        @NotEmpty(message = "El menú debe tener al menos un plato")
        List<@NotNull Long> platoIds
) {
}
