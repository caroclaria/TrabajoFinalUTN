package com.utn.tpfinal.dtos.regimen.response;

public record IngredienteResponseDTO(
        Long alimentoId,
        String alimentoNombre,
        Double cantidad,
        String unidadMedida
) {
}
