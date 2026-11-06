package com.utn.tpfinal.dtos.paciente;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PacienteEdit {

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    private String diagnosticoBase;
    // La fecha de alta se registra por su propio endpoint (PATCH /api/pacientes/{id}/alta)
}
