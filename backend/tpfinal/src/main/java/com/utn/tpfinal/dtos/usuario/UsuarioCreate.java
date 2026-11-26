package com.utn.tpfinal.dtos.usuario;

import com.utn.tpfinal.enums.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioCreate {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}