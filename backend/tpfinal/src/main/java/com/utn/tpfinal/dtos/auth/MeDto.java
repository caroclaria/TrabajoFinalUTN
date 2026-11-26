package com.utn.tpfinal.dtos.auth;

import com.utn.tpfinal.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

// Perfil del usuario logueado. Los datos profesionales solo vienen si es NUTRICIONISTA.
@Data
@AllArgsConstructor
public class MeDto {

    private Long id;
    private String username;
    private Rol rol;
    private String nombre;
    private String apellido;
    private String matricula;
}
