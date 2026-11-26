package com.utn.tpfinal.dtos.paciente;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PacienteDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String diagnosticoBase;
    private Long historiaClinicaId;
    private LocalDate fechaIngreso;
    private LocalDate fechaAlta;
    // Cama que ocupa actualmente (null si no tiene)
    private Long camaId;
    private String camaNumero;
}
