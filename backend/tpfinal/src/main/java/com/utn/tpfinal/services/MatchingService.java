package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.response.ConflictoDTO;
import com.utn.tpfinal.dtos.regimen.response.MatchingResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoEvaluadoDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoReemplazoDTO;
import com.utn.tpfinal.entities.IngredientePlato;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoPreferencia;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Motor de detección de conflictos (RF05).
//
// Idea: cada plato del menú se desagrega en sus ingredientes (alimentos). Se cruzan esos alimentos
// contra las preferencias del paciente marcadas como ALERGIA, INTOLERANCIA o RECHAZO.
// Si un plato tiene algún conflicto, se sugieren reemplazos: platos que se sirven en el mismo
// régimen y turno (en otros días del ciclo) y que no tienen ningún alimento conflictivo.
@Service
@RequiredArgsConstructor
public class MatchingService {

    // Cantidad máxima de reemplazos sugeridos por plato, para no saturar la pantalla
    static final int MAX_REEMPLAZOS = 5;

    private final PacienteRepository pacienteRepository;
    private final MenuRepository menuRepository;
    private final PreferenciaAlimentariaRepository preferenciaRepository;

    // Evalúa un menú cualquiera (elegido por id) contra las preferencias del paciente
    @Transactional(readOnly = true)
    public MatchingResponseDTO evaluarMenu(Long pacienteId, Long menuId) {
        Paciente paciente = buscarPaciente(pacienteId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Menú no encontrado con id " + menuId));
        return evaluar(paciente, menu);
    }

    // Evalúa el menú que le CORRESPONDE al paciente según su régimen indicado, para un turno y día del ciclo
    @Transactional(readOnly = true)
    public MatchingResponseDTO evaluarMenuAsignado(Long pacienteId, Integer numeroLista, TurnoComida turno) {
        Paciente paciente = buscarPaciente(pacienteId);
        if (paciente.getTipoRegimen() == null) {
            throw new ConflictoException("El paciente no tiene un régimen indicado");
        }
        Menu menu = menuRepository
                .findByNumeroListaAndTipoRegimenAndTurnoComida(numeroLista, paciente.getTipoRegimen(), turno)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay menú cargado para la lista "
                        + numeroLista + ", régimen " + paciente.getTipoRegimen() + " y turno " + turno));
        return evaluar(paciente, menu);
    }

    // ==========================================
    // NÚCLEO DEL MATCHING
    // ==========================================

    private MatchingResponseDTO evaluar(Paciente paciente, Menu menu) {
        // alimentoId -> preferencia, solo las que generan conflicto (ACEPTADO no molesta)
        Map<Long, PreferenciaAlimentaria> restricciones = preferenciaRepository.findByPacienteId(paciente.getId())
                .stream()
                .filter(p -> p.getTipo() != TipoPreferencia.ACEPTADO)
                .collect(Collectors.toMap(p -> p.getAlimento().getId(), p -> p));

        // Los candidatos a reemplazo se calculan una sola vez y solo si hace falta
        List<Plato> candidatos = null;
        Set<Long> platosDelMenu = menu.getPlatos().stream().map(Plato::getId).collect(Collectors.toSet());

        List<PlatoEvaluadoDTO> evaluados = new ArrayList<>();
        for (Plato plato : menu.getPlatos()) {
            List<ConflictoDTO> conflictos = detectarConflictos(plato, restricciones);
            SeveridadConflicto severidad = masGrave(conflictos);

            List<PlatoReemplazoDTO> reemplazos = List.of();
            if (!conflictos.isEmpty()) {
                if (candidatos == null) {
                    candidatos = menuRepository.platosDelRegimenYTurno(menu.getTipoRegimen(), menu.getTurnoComida());
                }
                reemplazos = sugerirReemplazos(candidatos, platosDelMenu, restricciones);
            }
            evaluados.add(new PlatoEvaluadoDTO(plato.getId(), plato.getNombre(), conflictos.isEmpty(),
                    severidad, conflictos, reemplazos));
        }

        SeveridadConflicto severidadMenu = evaluados.stream()
                .map(PlatoEvaluadoDTO::severidad)
                .max(Comparator.naturalOrder())
                .orElse(SeveridadConflicto.NINGUNA);

        return new MatchingResponseDTO(paciente.getId(), menu.getId(), menu.getNumeroLista(),
                menu.getTipoRegimen(), menu.getTurnoComida(),
                severidadMenu == SeveridadConflicto.NINGUNA, severidadMenu, evaluados);
    }

    // Ingredientes del plato que el paciente tiene marcados como alergia/intolerancia/rechazo,
    // ordenados del más grave al menos grave
    private List<ConflictoDTO> detectarConflictos(Plato plato, Map<Long, PreferenciaAlimentaria> restricciones) {
        Set<Long> yaReportados = new HashSet<>();
        List<ConflictoDTO> conflictos = new ArrayList<>();
        for (IngredientePlato ing : plato.getIngredientes()) {
            Long alimentoId = ing.getAlimento().getId();
            PreferenciaAlimentaria pref = restricciones.get(alimentoId);
            if (pref != null && yaReportados.add(alimentoId)) {
                conflictos.add(new ConflictoDTO(alimentoId, ing.getAlimento().getNombre(), pref.getTipo(),
                        SeveridadConflicto.desde(pref.getTipo())));
            }
        }
        conflictos.sort(Comparator.comparing(ConflictoDTO::severidad).reversed());
        return conflictos;
    }

    // Platos del mismo régimen y turno que no están ya en el menú y no tienen NINGÚN conflicto
    private List<PlatoReemplazoDTO> sugerirReemplazos(List<Plato> candidatos, Set<Long> platosDelMenu,
                                                       Map<Long, PreferenciaAlimentaria> restricciones) {
        return candidatos.stream()
                .filter(p -> !platosDelMenu.contains(p.getId()))
                .filter(p -> detectarConflictos(p, restricciones).isEmpty())
                .limit(MAX_REEMPLAZOS)
                .map(p -> new PlatoReemplazoDTO(p.getId(), p.getNombre()))
                .toList();
    }

    private SeveridadConflicto masGrave(List<ConflictoDTO> conflictos) {
        return conflictos.stream()
                .map(ConflictoDTO::severidad)
                .max(Comparator.naturalOrder())
                .orElse(SeveridadConflicto.NINGUNA);
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + id));
    }
}
