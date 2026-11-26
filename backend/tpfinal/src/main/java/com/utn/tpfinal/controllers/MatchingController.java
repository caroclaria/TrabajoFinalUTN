package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.regimen.response.MatchingResponseDTO;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.services.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// Motor de matching (RF05)
@RestController
@RequestMapping("/api/pacientes/{pacienteId}/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    // Menú que le corresponde al paciente por su régimen:
    // GET /api/pacientes/5/matching?numeroLista=3&turno=ALMUERZO
    @GetMapping
    public MatchingResponseDTO evaluarMenuAsignado(@PathVariable("pacienteId") Long pacienteId,
                                                   @RequestParam("numeroLista") Integer numeroLista,
                                                   @RequestParam("turno") TurnoComida turno) {
        return matchingService.evaluarMenuAsignado(pacienteId, numeroLista, turno);
    }

    // Cualquier menú puntual contra el paciente:
    // GET /api/pacientes/5/matching/menus/12
    @GetMapping("/menus/{menuId}")
    public MatchingResponseDTO evaluarMenu(@PathVariable("pacienteId") Long pacienteId,
                                           @PathVariable("menuId") Long menuId) {
        return matchingService.evaluarMenu(pacienteId, menuId);
    }
}
