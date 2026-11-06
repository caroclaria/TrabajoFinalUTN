package com.utn.tpfinal.dtos.auth;

import com.utn.tpfinal.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {

    private String token;
    private String username;
    private Rol rol;
}