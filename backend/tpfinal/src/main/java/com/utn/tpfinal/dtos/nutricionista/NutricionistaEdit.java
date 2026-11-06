package com.utn.tpfinal.dtos.nutricionista;

import lombok.Data;

@Data
public class NutricionistaEdit {

    private String nombre;
    private String apellido;
    // Sin username ni password: no se editan por este medio
}