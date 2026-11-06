package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.nutricionista.NutricionistaCreate;
import com.utn.tpfinal.dtos.nutricionista.NutricionistaDto;
import com.utn.tpfinal.dtos.nutricionista.NutricionistaEdit;
import com.utn.tpfinal.services.NutricionistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutricionistas")
public class NutricionistaController {

    private final NutricionistaService nutricionistaService;

    public NutricionistaController(NutricionistaService nutricionistaService) {
        this.nutricionistaService = nutricionistaService;
    }

    @PostMapping
    public ResponseEntity<NutricionistaDto> crear(@Valid @RequestBody NutricionistaCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nutricionistaService.crear(dto));
    }

    @GetMapping
    public List<NutricionistaDto> listar() {
        return nutricionistaService.listarTodos();
    }

    @GetMapping("/{id}")
    public NutricionistaDto buscarPorId(@PathVariable("id") Long id) {
        return nutricionistaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public NutricionistaDto editar(@PathVariable("id") Long id, @Valid @RequestBody NutricionistaEdit dto) {
        return nutricionistaService.editar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        nutricionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}