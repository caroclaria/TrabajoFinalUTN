package com.utn.tpfinal.dtos.regimen.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public record AlimentoResponseDTO(
        Long id,
        String nombre
) {}