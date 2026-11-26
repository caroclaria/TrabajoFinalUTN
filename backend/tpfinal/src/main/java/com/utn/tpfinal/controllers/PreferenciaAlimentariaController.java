package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.regimen.request.PreferenciaCreateDTO;
import com.utn.tpfinal.dtos.regimen.request.PreferenciaUpdateDTO;
import com.utn.tpfinal.dtos.regimen.response.PreferenciaResponseDTO;
import com.utn.tpfinal.services.PreferenciaAlimentariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Las preferencias cuelgan del paciente: /api/pacientes/{pacienteId}/preferencias
@RestController
@RequestMapping("/api/pacientes/{pacienteId}/preferencias")
@RequiredArgsConstructor
public class PreferenciaAlimentariaController {

    private final PreferenciaAlimentariaService preferenciaService;

    @GetMapping
    public List<PreferenciaResponseDTO> listar(@PathVariable("pacienteId") Long pacienteId) {
        return preferenciaService.listarPorPaciente(pacienteId);
    }

    @PostMapping
    public ResponseEntity<PreferenciaResponseDTO> crear(@PathVariable("pacienteId") Long pacienteId,
                                                        @Valid @RequestBody PreferenciaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenciaService.crear(pacienteId, dto));
    }

    @PutMapping("/{preferenciaId}")
    public PreferenciaResponseDTO actualizar(@PathVariable("pacienteId") Long pacienteId,
                                             @PathVariable("preferenciaId") Long preferenciaId,
                                             @Valid @RequestBody PreferenciaUpdateDTO dto) {
        return preferenciaService.actualizar(pacienteId, preferenciaId, dto);
    }

    @DeleteMapping("/{preferenciaId}")
    public ResponseEntity<Void> eliminar(@PathVariable("pacienteId") Long pacienteId,
                                         @PathVariable("preferenciaId") Long preferenciaId) {
        preferenciaService.eliminar(pacienteId, preferenciaId);
        return ResponseEntity.noContent().build();
    }
}
