package com.utn.tpfinal.dtos.usuario;

import com.utn.tpfinal.enums.Rol;
import lombok.Data;

@Data
public class UsuarioEdit {

    private Rol rol;
    private Boolean activo;
    // Sin username ni password: no se editan por este medio
}