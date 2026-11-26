package com.utn.tpfinal.dtos.evolucion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EvolucionDto {

    private Long id;
    private LocalDateTime fechaHora;
    private String nota;
    // Firma del profesional
    private String nutricionistaNombre;
    private String nutricionistaApellido;
    private String nutricionistaMatricula;
}
