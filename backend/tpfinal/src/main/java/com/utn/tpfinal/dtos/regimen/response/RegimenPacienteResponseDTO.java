package com.utn.tpfinal.dtos.regimen.response;

import com.utn.tpfinal.enums.TipoRegimen;

public record RegimenPacienteResponseDTO(
        Long pacienteId,
        String pacienteNombreCompleto,
        TipoRegimen tipoRegimen   // null si todavía no tiene régimen indicado
) {}
