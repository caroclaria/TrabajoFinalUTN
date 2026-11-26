package com.utn.tpfinal.dtos.regimen.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IngredienteRequestDTO(
        @NotNull(message = "Debe indicar el alimento")
        Long alimentoId,

        @NotNull(message = "Debe indicar la cantidad")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Double cantidad,

        @Size(max = 30, message = "La unidad de medida admite hasta 30 caracteres")
        String unidadMedida
) {}
