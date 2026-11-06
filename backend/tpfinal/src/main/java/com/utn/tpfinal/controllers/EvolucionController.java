package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.evolucion.EvolucionCreate;
import com.utn.tpfinal.dtos.evolucion.EvolucionDto;
import com.utn.tpfinal.services.EvolucionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Solo hay GET y POST: las evoluciones son inmutables (no se editan ni se borran)
@RestController
@RequestMapping("/api/pacientes/{pacienteId}/evoluciones")
public class EvolucionController {

    private final EvolucionService evolucionService;

    public EvolucionController(EvolucionService evolucionService) {
        this.evolucionService = evolucionService;
    }

    @GetMapping
    public List<EvolucionDto> listar(@PathVariable("pacienteId") Long pacienteId) {
        return evolucionService.listar(pacienteId);
    }

    // authentication.getName() es el "subject" del JWT, o sea el username del usuario logueado
    @PostMapping
    public ResponseEntity<EvolucionDto> crear(@PathVariable("pacienteId") Long pacienteId,
                                              @Valid @RequestBody EvolucionCreate dto,
                                              Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(evolucionService.crear(pacienteId, dto, authentication.getName()));
    }
}
