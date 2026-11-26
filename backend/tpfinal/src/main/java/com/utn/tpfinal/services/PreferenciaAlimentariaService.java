package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.PreferenciaCreateDTO;
import com.utn.tpfinal.dtos.regimen.request.PreferenciaUpdateDTO;
import com.utn.tpfinal.dtos.regimen.response.PreferenciaResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.mappers.RegimenMapper;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Preferencias alimentarias del paciente (RF04): Aceptado, Rechazo, Alergia, Intolerancia.
// Son la entrada del motor de matching (RF05).
@Service
@RequiredArgsConstructor
public class PreferenciaAlimentariaService {

    private final PreferenciaAlimentariaRepository preferenciaRepository;
    private final PacienteRepository pacienteRepository;
    private final AlimentoRepository alimentoRepository;

    @Transactional(readOnly = true)
    public List<PreferenciaResponseDTO> listarPorPaciente(Long pacienteId) {
        validarPacienteExiste(pacienteId);
        return preferenciaRepository.findByPacienteId(pacienteId).stream()
                .map(RegimenMapper::aDto)
                .toList();
    }

    @Transactional
    public PreferenciaResponseDTO crear(Long pacienteId, PreferenciaCreateDTO dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + pacienteId));
        Alimento alimento = alimentoRepository.findById(dto.alimentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alimento no encontrado con id " + dto.alimentoId()));

        // Un paciente tiene una sola preferencia por alimento: para cambiarla se edita la existente
        if (preferenciaRepository.existsByPaciente_IdAndAlimento_Id(pacienteId, alimento.getId())) {
            throw new ConflictoException("El paciente ya tiene una preferencia registrada para '"
                    + alimento.getNombre() + "'. Edítela en lugar de crear otra");
        }

        PreferenciaAlimentaria guardada = preferenciaRepository.save(
                new PreferenciaAlimentaria(paciente, alimento, dto.tipo()));
        return RegimenMapper.aDto(guardada);
    }

    @Transactional
    public PreferenciaResponseDTO actualizar(Long pacienteId, Long preferenciaId, PreferenciaUpdateDTO dto) {
        PreferenciaAlimentaria preferencia = obtener(pacienteId, preferenciaId);
        preferencia.setTipo(dto.tipo());
        return RegimenMapper.aDto(preferenciaRepository.save(preferencia));
    }

    @Transactional
    public void eliminar(Long pacienteId, Long preferenciaId) {
        preferenciaRepository.delete(obtener(pacienteId, preferenciaId));
    }

    // Busca la preferencia verificando que sea de ESE paciente
    // (evita modificar la preferencia de otro paciente cambiando el id en la URL)
    private PreferenciaAlimentaria obtener(Long pacienteId, Long preferenciaId) {
        validarPacienteExiste(pacienteId);
        return preferenciaRepository.findByIdAndPaciente_Id(preferenciaId, pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Preferencia " + preferenciaId + " no encontrada para el paciente " + pacienteId));
    }

    private void validarPacienteExiste(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new RecursoNoEncontradoException("Paciente no encontrado con id " + pacienteId);
        }
    }
}
