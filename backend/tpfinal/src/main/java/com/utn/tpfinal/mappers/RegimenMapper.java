package com.utn.tpfinal.mappers;

import com.utn.tpfinal.dtos.regimen.response.AlimentoResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.IngredienteResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.MenuResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PreferenciaResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import com.utn.tpfinal.enums.SeveridadConflicto;

// Conversión entidad -> DTO del módulo de regímenes, menús y preferencias.
// Está centralizado para que Plato, Menú y Matching devuelvan exactamente el mismo formato.
public final class RegimenMapper {

    private RegimenMapper() {
    }

    public static AlimentoResponseDTO aDto(Alimento a) {
        return new AlimentoResponseDTO(a.getId(), a.getNombre());
    }

    public static PlatoResponseDTO aDto(Plato plato) {
        return new PlatoResponseDTO(
                plato.getId(),
                plato.getNombre(),
                plato.getIngredientes().stream()
                        .map(ing -> new IngredienteResponseDTO(
                                ing.getAlimento().getId(),
                                ing.getAlimento().getNombre(),
                                ing.getCantidad(),
                                ing.getUnidadMedida()))
                        .toList());
    }

    public static MenuResponseDTO aDto(Menu menu) {
        return new MenuResponseDTO(
                menu.getId(),
                menu.getNumeroLista(),
                menu.getTipoRegimen(),
                menu.getTurnoComida(),
                menu.getPlatos().stream().map(RegimenMapper::aDto).toList());
    }

    public static PreferenciaResponseDTO aDto(PreferenciaAlimentaria p) {
        return new PreferenciaResponseDTO(
                p.getId(),
                p.getPaciente().getId(),
                p.getAlimento().getId(),
                p.getAlimento().getNombre(),
                p.getTipo(),
                SeveridadConflicto.desde(p.getTipo()));
    }
}
