package com.utn.tpfinal.dtos.regimen.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PlatoResponseDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("nombre") String nombre,
        @JsonProperty("ingredientes") List<IngredienteResponseDTO> ingredientes) {
}
