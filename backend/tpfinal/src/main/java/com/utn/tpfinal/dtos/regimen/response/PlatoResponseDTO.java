package com.utn.tpfinal.dtos.regimen.response;

import java.util.List;

public record PlatoResponseDTO(
        Long id,
        String nombre,
        List<IngredienteResponseDTO> ingredientes) {
}
