package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.regimen.request.AlimentoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.AlimentoResponseDTO;
import com.utn.tpfinal.services.AlimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alimentos")
@RequiredArgsConstructor
public class AlimentoController {

    private final AlimentoService alimentoService;

    @PostMapping
    public ResponseEntity<AlimentoResponseDTO> crear(@Valid @RequestBody AlimentoRequestDTO request) {
        AlimentoResponseDTO nuevo = alimentoService.crear(request);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlimentoResponseDTO>> obtenerTodos() {
        List<AlimentoResponseDTO> lista = alimentoService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlimentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        AlimentoResponseDTO alimento = alimentoService.obtenerPorId(id);
        return ResponseEntity.ok(alimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlimentoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlimentoRequestDTO request) {
        AlimentoResponseDTO actualizado = alimentoService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alimentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}