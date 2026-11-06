package com.utn.tpfinal.dtos.cama;

import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CamaDto {

    private Long id;
    private String numero;
    private Integer piso;
    private Sector sector;
    private EstadoCama estado;
    // Paciente que ocupa la cama (null si no hay)
    private Long pacienteId;
    private String pacienteNombre;
}
