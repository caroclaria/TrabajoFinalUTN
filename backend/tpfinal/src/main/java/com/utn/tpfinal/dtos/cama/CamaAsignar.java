package com.utn.tpfinal.dtos.cama;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CamaAsignar {

    @NotNull(message = "El paciente es obligatorio")
    private Long pacienteId;
}
