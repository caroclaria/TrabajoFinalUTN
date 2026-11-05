package com.utn.tpfinal.dtos.regimen.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IngredienteResponseDTO(
        @JsonProperty("alimentoId") Long alimentoId,
        @JsonProperty("alimentoNombre") String alimentoNombre,
        @JsonProperty("cantidad") Double cantidad,
        @JsonProperty("unidadMedida") String unidadMedida
) {
}
