package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.RegimenPacienteRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.RegimenPacienteResponseDTO;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Indicación del régimen dietoterápico de un paciente (General, Hiposódico, Celíaco, etc.).
// El régimen define qué menú le corresponde y es lo que usan el matching y la cocina.
@Service
@RequiredArgsConstructor
public class RegimenPacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public RegimenPacienteResponseDTO obtener(Long pacienteId) {
        return aDto(buscarPaciente(pacienteId));
    }

    @Transactional
    public RegimenPacienteResponseDTO indicar(Long pacienteId, RegimenPacienteRequestDTO dto) {
        Paciente paciente = buscarPaciente(pacienteId);
        if (paciente.getFechaAlta() != null) {
            throw new ConflictoException("No se puede indicar un régimen a un paciente dado de alta");
        }
        paciente.setTipoRegimen(dto.tipoRegimen());
        return aDto(pacienteRepository.save(paciente));
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + id));
    }

    private RegimenPacienteResponseDTO aDto(Paciente p) {
        return new RegimenPacienteResponseDTO(p.getId(), p.getApellido() + ", " + p.getNombre(), p.getTipoRegimen());
    }
}
