package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.AlimentoRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.AlimentoResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.mappers.RegimenMapper;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Catálogo de alimentos (RF04): la unidad mínima con la que se arman los platos
// y sobre la que se registran las preferencias/alergias de los pacientes.
@Service
@RequiredArgsConstructor
public class AlimentoService {

    private final AlimentoRepository alimentoRepository;
    private final PlatoRepository platoRepository;
    private final PreferenciaAlimentariaRepository preferenciaRepository;

    @Transactional
    public AlimentoResponseDTO crear(AlimentoRequestDTO request) {
        String nombre = normalizarTexto(request.nombre());
        validarNombreUnico(nombre, null);
        return RegimenMapper.aDto(alimentoRepository.save(new Alimento(nombre)));
    }

    @Transactional(readOnly = true)
    public List<AlimentoResponseDTO> obtenerTodos() {
        return alimentoRepository.findAllByOrderByNombreAsc().stream()
                .map(RegimenMapper::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlimentoResponseDTO obtenerPorId(Long id) {
        return RegimenMapper.aDto(obtener(id));
    }

    @Transactional
    public AlimentoResponseDTO actualizar(Long id, AlimentoRequestDTO request) {
        Alimento alimento = obtener(id);
        String nombre = normalizarTexto(request.nombre());
        validarNombreUnico(nombre, id);
        alimento.setNombre(nombre);
        return RegimenMapper.aDto(alimentoRepository.save(alimento));
    }

    // No se puede borrar un alimento que es ingrediente de algún plato o que figura en
    // las preferencias de un paciente: se perdería información clínica o se romperían platos.
    @Transactional
    public void eliminar(Long id) {
        Alimento alimento = obtener(id);
        if (platoRepository.existsByIngredientes_Alimento_Id(id)) {
            throw new ConflictoException("No se puede eliminar '" + alimento.getNombre()
                    + "': es ingrediente de al menos un plato");
        }
        if (preferenciaRepository.existsByAlimento_Id(id)) {
            throw new ConflictoException("No se puede eliminar '" + alimento.getNombre()
                    + "': figura en las preferencias de al menos un paciente");
        }
        alimentoRepository.delete(alimento);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE APOYO
    // ==========================================

    private Alimento obtener(Long id) {
        return alimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alimento no encontrado con id " + id));
    }

    private String normalizarTexto(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private void validarNombreUnico(String nombre, Long idActual) {
        alimentoRepository.findByNombreIgnoreCase(nombre).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new ConflictoException("Ya existe un alimento registrado con el nombre: " + nombre);
            }
        });
    }
}
