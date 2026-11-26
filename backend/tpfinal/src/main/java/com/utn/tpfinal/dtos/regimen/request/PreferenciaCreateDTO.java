package com.utn.tpfinal.dtos.regimen.request;

import com.utn.tpfinal.enums.TipoPreferencia;
import jakarta.validation.constraints.NotNull;

public record PreferenciaCreateDTO(
        @NotNull(message = "Debe indicar el alimento")
        Long alimentoId,

        @NotNull(message = "Debe indicar el tipo de preferencia")
        TipoPreferencia tipo
) {}
