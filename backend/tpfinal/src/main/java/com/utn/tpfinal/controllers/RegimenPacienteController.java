package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.regimen.request.RegimenPacienteRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.RegimenPacienteResponseDTO;
import com.utn.tpfinal.services.RegimenPacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/regimen")
@RequiredArgsConstructor
public class RegimenPacienteController {

    private final RegimenPacienteService regimenPacienteService;

    @GetMapping
    public RegimenPacienteResponseDTO obtener(@PathVariable("pacienteId") Long pacienteId) {
        return regimenPacienteService.obtener(pacienteId);
    }

    @PutMapping
    public RegimenPacienteResponseDTO indicar(@PathVariable("pacienteId") Long pacienteId,
                                              @Valid @RequestBody RegimenPacienteRequestDTO dto) {
        return regimenPacienteService.indicar(pacienteId, dto);
    }
}
