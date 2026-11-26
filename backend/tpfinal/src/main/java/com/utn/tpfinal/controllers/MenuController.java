package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.regimen.request.MenuRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.MenuResponseDTO;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.services.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponseDTO> crear(@Valid @RequestBody MenuRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.crear(request));
    }

    // GET /api/menus                                         -> todos
    // GET /api/menus?tipoRegimen=HIPOSODICO&turno=ALMUERZO   -> filtrados (todos los filtros son opcionales)
    @GetMapping
    public List<MenuResponseDTO> listar(
            @RequestParam(name = "numeroLista", required = false) Integer numeroLista,
            @RequestParam(name = "tipoRegimen", required = false) TipoRegimen tipoRegimen,
            @RequestParam(name = "turno", required = false) TurnoComida turno) {
        return menuService.listar(numeroLista, tipoRegimen, turno);
    }

    @GetMapping("/{id}")
    public MenuResponseDTO buscarPorId(@PathVariable("id") Long id) {
        return menuService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public MenuResponseDTO actualizar(@PathVariable("id") Long id, @Valid @RequestBody MenuRequestDTO request) {
        return menuService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        menuService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
