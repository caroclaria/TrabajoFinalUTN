package com.utn.tpfinal.dtos.usuario;

import com.utn.tpfinal.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDto {

    private Long id;
    private String username;
    private Rol rol;
    private Boolean activo;
    // Sin password: nunca debe mostrarse
}