package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.cama.CamaAsignar;
import com.utn.tpfinal.dtos.cama.CamaCreate;
import com.utn.tpfinal.dtos.cama.CamaDto;
import com.utn.tpfinal.dtos.cama.CamaEdit;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;
import com.utn.tpfinal.services.CamaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camas")
public class CamaController {

    private final CamaService camaService;

    public CamaController(CamaService camaService) {
        this.camaService = camaService;
    }

    @PostMapping
    public ResponseEntity<CamaDto> crear(@Valid @RequestBody CamaCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(camaService.crear(dto));
    }

    // GET /api/camas                          → todas
    // GET /api/camas?piso=2&sector=CLINICA    → filtradas para el panel (RF02)
    @GetMapping
    public List<CamaDto> listar(@RequestParam(name = "piso", required = false) Integer piso,
                                @RequestParam(name = "sector", required = false) Sector sector) {
        if (piso != null && sector != null) {
            return camaService.listarPorPisoYSector(piso, sector);
        }
        return camaService.listarTodos();
    }

    @GetMapping("/{id}")
    public CamaDto buscarPorId(@PathVariable("id") Long id) {
        return camaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CamaDto editar(@PathVariable("id") Long id, @Valid @RequestBody CamaEdit dto) {
        return camaService.editar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public CamaDto cambiarEstado(@PathVariable("id") Long id,
                                 @RequestParam(name = "estado") EstadoCama estado) {
        return camaService.cambiarEstado(id, estado);
    }

    // Asignar / desasignar paciente (RF02): POST /api/camas/5/asignar  con body {"pacienteId": 3}
    @PostMapping("/{id}/asignar")
    public CamaDto asignar(@PathVariable("id") Long id, @Valid @RequestBody CamaAsignar dto) {
        return camaService.asignarPaciente(id, dto.getPacienteId());
    }

    @PostMapping("/{id}/desasignar")
    public CamaDto desasignar(@PathVariable("id") Long id) {
        return camaService.desasignarPaciente(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        camaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}