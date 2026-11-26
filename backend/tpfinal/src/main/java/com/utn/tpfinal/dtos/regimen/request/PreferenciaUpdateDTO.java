package com.utn.tpfinal.dtos.regimen.request;

import com.utn.tpfinal.enums.TipoPreferencia;
import jakarta.validation.constraints.NotNull;

// Solo se puede cambiar el tipo. Para otro alimento se borra y se crea una nueva preferencia.
public record PreferenciaUpdateDTO(
        @NotNull(message = "Debe indicar el tipo de preferencia")
        TipoPreferencia tipo
) {}
