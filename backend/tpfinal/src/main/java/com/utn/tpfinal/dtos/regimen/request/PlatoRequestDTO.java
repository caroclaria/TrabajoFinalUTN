package com.utn.tpfinal.dtos.regimen.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlatoRequestDTO(
        @NotBlank(message = "EL nombre no puede estar vacio")
        @JsonProperty("nombre")
        String nombre,

        @NotEmpty(message = "Debe tener al menos un ingrediente")
        @JsonProperty("ingredientes")
        List<IngredienteRequestDTO> ingredientes) {
}
