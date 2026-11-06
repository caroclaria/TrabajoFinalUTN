package com.utn.tpfinal.dtos.evolucion;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EvolucionCreate {

    @NotBlank(message = "La nota de evolución es obligatoria")
    private String nota;
    // Sin nutricionista ni fecha: los completa el servidor (firma automática)
}
