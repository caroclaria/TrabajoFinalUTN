package com.utn.tpfinal.dtos.regimen.request;

import com.utn.tpfinal.enums.TipoRegimen;
import jakarta.validation.constraints.NotNull;

public record RegimenPacienteRequestDTO(
        @NotNull(message = "Debe indicar el régimen")
        TipoRegimen tipoRegimen
) {}
