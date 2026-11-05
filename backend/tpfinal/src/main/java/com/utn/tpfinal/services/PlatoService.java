package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.PlatoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.IngredienteResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.IngredientePlato;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.dtos.regimen.request.IngredienteRequestDTO;
import com.utn.tpfinal.exception.ResourceNotFoundException;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatoService{
    private final  PlatoRepository platoRepository;
    private final AlimentoRepository alimentoRepository;

    @Transactional
    public PlatoResponseDTO create(PlatoRequestDTO request) {
        String normalizedName = normalizeText(request.nombre());

        if (platoRepository.existsByNombreIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Ya existe plato con este nombre: " + normalizedName);
        }
        // 1. Convertimos los DTOs a la lista de entidades IngredientePlato
        List<IngredientePlato> ingredientList = buildIngredientList(request.ingredientes());

        Plato plato = new Plato(normalizedName, ingredientList);


        Plato savedPlato = platoRepository.save(plato);
        return mapToResponseDTO(savedPlato);
    }

    @Transactional(readOnly = true)
    public List<PlatoResponseDTO> getAll() {
        return platoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlatoResponseDTO getById(Long id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
        return mapToResponseDTO(plato);
    }

    @Transactional
    public PlatoResponseDTO update(Long id, PlatoRequestDTO request) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));

        String normalizedName = normalizeText(request.nombre());

        if (!plato.getNombre().equalsIgnoreCase(normalizedName) &&
                platoRepository.existsByNombreIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Another dish already exists with name: " + normalizedName);
        }

        List<IngredientePlato> updatedIngredients = buildIngredientList(request.ingredientes());

        plato.setNombre(normalizedName);
        plato.getIngredientes().clear();
        plato.getIngredientes().addAll(updatedIngredients);

        Plato updatedPlato = platoRepository.save(plato);
        return mapToResponseDTO(updatedPlato);
    }

    @Transactional
    public void delete(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dish not found with id: " + id);
        }
        platoRepository.deleteById(id);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private List<IngredientePlato> buildIngredientList(List<IngredienteRequestDTO> ingredientDTOs) {
        List<IngredientePlato> ingredientList = new ArrayList<>();

        for (IngredienteRequestDTO dto : ingredientDTOs) {
            Alimento alimento = alimentoRepository.findById(dto.alimentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + dto.alimentoId()));

            IngredientePlato ingredient = IngredientePlato.builder()
                    .alimento(alimento)
                    .cantidad(dto.cantidad())
                    .unidadMedida(dto.unidadMedida() != null ? dto.unidadMedida().trim() : null)
                    .build();

            ingredientList.add(ingredient);
        }

        return ingredientList;
    }

    private String normalizeText(String text) {
        return text != null ? text.trim() : "";
    }

    public PlatoResponseDTO mapToResponseDTO(Plato plato) {
        List<IngredienteResponseDTO> ingredientResponseDTOs = plato.getIngredientes().stream()
                .map(ing -> new IngredienteResponseDTO(
                        ing.getAlimento().getId(),
                        ing.getAlimento().getNombre(),
                        ing.getCantidad(),
                        ing.getUnidadMedida()
                ))
                .toList();

        return new PlatoResponseDTO(
                plato.getId(),
                plato.getNombre(),
                ingredientResponseDTOs
        );
    }
}
