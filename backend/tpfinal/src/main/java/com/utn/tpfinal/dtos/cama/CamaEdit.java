package com.utn.tpfinal.dtos.cama;

import com.utn.tpfinal.enums.Sector;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CamaEdit {

    @Size(max = 20, message = "El número no puede superar los 20 caracteres")
    private String numero;
    private Integer piso;
    private Sector sector;
    // El estado se cambia por su propio endpoint (PATCH /api/camas/{id}/estado)
}