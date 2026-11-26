package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.IngredienteRequestDTO;
import com.utn.tpfinal.dtos.regimen.request.PlatoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.IngredientePlato;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.mappers.RegimenMapper;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Platos del catálogo (RF04): cada plato se desagrega en ingredientes (alimento + cantidad).
@Service
@RequiredArgsConstructor
public class PlatoService {

    private final PlatoRepository platoRepository;
    private final AlimentoRepository alimentoRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public PlatoResponseDTO create(PlatoRequestDTO request) {
        String nombre = normalizeText(request.nombre());
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un plato con el nombre: " + nombre);
        }
        Plato plato = new Plato(nombre, buildIngredientList(request.ingredientes()));
        return RegimenMapper.aDto(platoRepository.save(plato));
    }

    @Transactional(readOnly = true)
    public List<PlatoResponseDTO> getAll() {
        return platoRepository.findAllByOrderByNombreAsc().stream()
                .map(RegimenMapper::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlatoResponseDTO getById(Long id) {
        return RegimenMapper.aDto(obtener(id));
    }

    @Transactional
    public PlatoResponseDTO update(Long id, PlatoRequestDTO request) {
        Plato plato = obtener(id);
        String nombre = normalizeText(request.nombre());

        if (!plato.getNombre().equalsIgnoreCase(nombre) && platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe otro plato con el nombre: " + nombre);
        }

        List<IngredientePlato> ingredientes = buildIngredientList(request.ingredientes());
        plato.setNombre(nombre);
        plato.getIngredientes().clear();
        plato.getIngredientes().addAll(ingredientes);

        return RegimenMapper.aDto(platoRepository.save(plato));
    }

    // Un plato que forma parte de algún menú no se puede borrar: primero hay que sacarlo del menú.
    @Transactional
    public void delete(Long id) {
        Plato plato = obtener(id);
        if (menuRepository.existsByPlatos_Id(id)) {
            throw new ConflictoException("No se puede eliminar '" + plato.getNombre()
                    + "': forma parte de al menos un menú");
        }
        platoRepository.delete(plato);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==========================================

    private Plato obtener(Long id) {
        return platoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado con id " + id));
    }

    private List<IngredientePlato> buildIngredientList(List<IngredienteRequestDTO> ingredientDTOs) {
        List<IngredientePlato> ingredientes = new ArrayList<>();
        Set<Long> alimentosVistos = new HashSet<>();

        for (IngredienteRequestDTO dto : ingredientDTOs) {
            if (!alimentosVistos.add(dto.alimentoId())) {
                throw new IllegalArgumentException("El alimento con id " + dto.alimentoId()
                        + " está repetido en los ingredientes");
            }
            Alimento alimento = alimentoRepository.findById(dto.alimentoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Alimento no encontrado con id " + dto.alimentoId()));

            ingredientes.add(IngredientePlato.builder()
                    .alimento(alimento)
                    .cantidad(dto.cantidad())
                    .unidadMedida(dto.unidadMedida() != null ? dto.unidadMedida().trim() : null)
                    .build());
        }
        return ingredientes;
    }

    private String normalizeText(String text) {
        return text != null ? text.trim() : "";
    }
}
