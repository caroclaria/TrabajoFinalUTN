package com.utn.tpfinal.controllers;


import com.utn.tpfinal.dtos.regimen.request.PlatoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoResponseDTO;
import com.utn.tpfinal.services.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;

    @PostMapping
    public ResponseEntity<PlatoResponseDTO> create(@Valid @RequestBody PlatoRequestDTO request) {
        return new ResponseEntity<>(platoService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> getAll() {
        return ResponseEntity.ok(platoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(platoService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PlatoRequestDTO request) {
        return ResponseEntity.ok(platoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        platoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}