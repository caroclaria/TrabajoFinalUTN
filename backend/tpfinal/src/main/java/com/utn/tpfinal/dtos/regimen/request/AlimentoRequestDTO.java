package com.utn.tpfinal.dtos.regimen.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlimentoRequestDTO(
        @NotBlank(message = "El nombre del alimento no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre
) {}