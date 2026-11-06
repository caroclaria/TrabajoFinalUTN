package com.utn.tpfinal.dtos.nutricionista;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NutricionistaDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String matricula;
    // Sin password: nunca debe mostrarse
}