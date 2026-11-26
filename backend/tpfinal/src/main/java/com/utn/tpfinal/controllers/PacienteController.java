package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.paciente.PacienteCreate;
import com.utn.tpfinal.dtos.paciente.PacienteDto;
import com.utn.tpfinal.dtos.paciente.PacienteEdit;
import com.utn.tpfinal.services.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteDto> crear(@Valid @RequestBody PacienteCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(dto));
    }

    // GET /api/pacientes                      → todos
    // GET /api/pacientes?soloInternados=true  → solo los que no tienen alta
    @GetMapping
    public List<PacienteDto> listar(@RequestParam(name = "soloInternados", defaultValue = "false") boolean soloInternados) {
        return pacienteService.listar(soloInternados);
    }

    @GetMapping("/{id}")
    public PacienteDto buscarPorId(@PathVariable("id") Long id) {
        return pacienteService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PacienteDto editar(@PathVariable("id") Long id, @Valid @RequestBody PacienteEdit dto) {
        return pacienteService.editar(id, dto);
    }

    @PatchMapping("/{id}/alta")
    public PacienteDto darDeAlta(@PathVariable("id") Long id) {
        return pacienteService.darDeAlta(id);
    }
}
