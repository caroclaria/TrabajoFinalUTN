package com.utn.tpfinal.dtos.cama;

import com.utn.tpfinal.enums.Sector;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CamaCreate {

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 20, message = "El número no puede superar los 20 caracteres")
    private String numero;

    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @NotNull(message = "El sector es obligatorio")
    private Sector sector;
    // El estado no se pide: toda cama nueva arranca DISPONIBLE
}