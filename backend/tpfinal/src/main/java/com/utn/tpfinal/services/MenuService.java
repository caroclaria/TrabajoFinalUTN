package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.MenuRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.MenuResponseDTO;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.mappers.RegimenMapper;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Menús cíclicos (RF04): para cada lista (día del ciclo), régimen y turno hay un único menú,
// compuesto por platos del catálogo.
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final PlatoRepository platoRepository;

    @Transactional
    public MenuResponseDTO crear(MenuRequestDTO request) {
        validarCombinacionUnica(request, null);
        Menu menu = new Menu(request.numeroLista(), request.tipoRegimen(), request.turnoComida(),
                buscarPlatos(request.platoIds()));
        return RegimenMapper.aDto(menuRepository.save(menu));
    }

    // Todos los parámetros son opcionales: sin filtros devuelve el catálogo completo
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> listar(Integer numeroLista, TipoRegimen tipoRegimen, TurnoComida turno) {
        return menuRepository.buscar(numeroLista, tipoRegimen, turno).stream()
                .map(RegimenMapper::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuResponseDTO buscarPorId(Long id) {
        return RegimenMapper.aDto(obtener(id));
    }

    @Transactional
    public MenuResponseDTO actualizar(Long id, MenuRequestDTO request) {
        Menu menu = obtener(id);
        validarCombinacionUnica(request, id);

        menu.setNumeroLista(request.numeroLista());
        menu.setTipoRegimen(request.tipoRegimen());
        menu.setTurnoComida(request.turnoComida());
        menu.getPlatos().clear();
        menu.getPlatos().addAll(buscarPlatos(request.platoIds()));

        return RegimenMapper.aDto(menuRepository.save(menu));
    }

    @Transactional
    public void eliminar(Long id) {
        menuRepository.delete(obtener(id));
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==========================================

    private Menu obtener(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Menú no encontrado con id " + id));
    }

    // RNF01: no puede haber dos menús para la misma lista + régimen + turno
    // (si no, no se sabría cuál servirle al paciente)
    private void validarCombinacionUnica(MenuRequestDTO request, Long idActual) {
        menuRepository.findByNumeroListaAndTipoRegimenAndTurnoComida(
                        request.numeroLista(), request.tipoRegimen(), request.turnoComida())
                .ifPresent(existente -> {
                    if (idActual == null || !existente.getId().equals(idActual)) {
                        throw new ConflictoException("Ya existe un menú para la lista " + request.numeroLista()
                                + ", régimen " + request.tipoRegimen() + " y turno " + request.turnoComida());
                    }
                });
    }

    private List<Plato> buscarPlatos(List<Long> platoIds) {
        Set<Long> vistos = new HashSet<>();
        List<Plato> platos = new ArrayList<>();
        for (Long platoId : platoIds) {
            if (!vistos.add(platoId)) {
                throw new IllegalArgumentException("El plato con id " + platoId + " está repetido en el menú");
            }
            platos.add(platoRepository.findById(platoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con id " + platoId)));
        }
        return platos;
    }
}
