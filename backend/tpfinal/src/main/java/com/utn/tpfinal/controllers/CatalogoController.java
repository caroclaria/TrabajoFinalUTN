package com.utn.tpfinal.controllers;

import com.utn.tpfinal.enums.TipoPreferencia;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Valores fijos del dominio, para que el frontend arme los selects sin hardcodearlos
@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @GetMapping("/regimenes")
    public List<TipoRegimen> regimenes() {
        return List.of(TipoRegimen.values());
    }

    @GetMapping("/turnos")
    public List<TurnoComida> turnos() {
        return List.of(TurnoComida.values());
    }

    @GetMapping("/tipos-preferencia")
    public List<TipoPreferencia> tiposPreferencia() {
        return List.of(TipoPreferencia.values());
    }
}
