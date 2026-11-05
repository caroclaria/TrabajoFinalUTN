package com.utn.tpfinal.dtos.regimen.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngredienteRequestDTO(
        @NotNull(message = "Food ID is required")
        @JsonProperty("alimentoId")
        Long alimentoId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        @JsonProperty("cantidad")
        Double cantidad,

        @JsonProperty("unidadMedida")
        String unidadMedida
) {}
