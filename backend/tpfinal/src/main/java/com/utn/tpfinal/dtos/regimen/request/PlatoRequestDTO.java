package com.utn.tpfinal.dtos.regimen.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlatoRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
        String nombre,

        @NotEmpty(message = "Debe tener al menos un ingrediente")
        List<@Valid IngredienteRequestDTO> ingredientes) {
}
