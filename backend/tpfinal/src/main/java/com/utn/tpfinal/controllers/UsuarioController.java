package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.usuario.UsuarioCreate;
import com.utn.tpfinal.dtos.usuario.UsuarioDto;
import com.utn.tpfinal.dtos.usuario.UsuarioEdit;
import com.utn.tpfinal.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
    }

    @GetMapping
    public List<UsuarioDto> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public UsuarioDto buscarPorId(@PathVariable("id") Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioDto editar(@PathVariable("id") Long id, @Valid @RequestBody UsuarioEdit dto) {
        return usuarioService.editar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}