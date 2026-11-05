package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.AlimentoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.AlimentoResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.repositories.AlimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.utn.tpfinal.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlimentoService {

    private final AlimentoRepository alimentoRepository;


    @Transactional
    public AlimentoResponseDTO crear(AlimentoRequestDTO request) {
        String nombreNormalizado = normalizarTexto(request.nombre());
        validarNombreUnico(nombreNormalizado, null);
        Alimento alimento = new Alimento(nombreNormalizado);
        return mapToResponseDTO(alimentoRepository.save(alimento));
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> obtenerTodos() {
        return alimentoRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlimentoResponseDTO obtenerPorId(Long id) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));
        return mapToResponseDTO(alimento);
    }

    @Transactional
    public AlimentoResponseDTO actualizar(Long id, AlimentoRequestDTO request) {
        Alimento alimento = alimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con id: " + id));

        String nuevoNombreNormalizado = normalizarTexto(request.nombre());

        validarNombreUnico(nuevoNombreNormalizado, id);

        alimento.setNombre(nuevoNombreNormalizado);
        Alimento actualizado = alimentoRepository.save(alimento);
        return mapToResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!alimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alimento no encontrado con id: " + id);
        }
        alimentoRepository.deleteById(id);
    }

    // ==========================================
    // LÓGICA ESPECIAL Y OPERACIONES AVANZADAS
    // ==========================================


    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return alimentoRepository.existsByNombreIgnoreCase(normalizarTexto(nombre));
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==========================================

    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        return texto.trim();
    }

    private void validarNombreUnico(String nombre, Long idActual) {
        alimentoRepository.findByNombreIgnoreCase(nombre).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new IllegalArgumentException("Ya existe un alimento registrado con el nombre: " + nombre);
            }
        });
    }

    private AlimentoResponseDTO mapToResponseDTO(Alimento alimento) {
        return new AlimentoResponseDTO(
                alimento.getId(),
                alimento.getNombre()
        );
    }
}
