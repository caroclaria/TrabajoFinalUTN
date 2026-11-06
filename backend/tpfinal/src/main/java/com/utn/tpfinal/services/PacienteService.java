package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.paciente.PacienteCreate;
import com.utn.tpfinal.dtos.paciente.PacienteDto;
import com.utn.tpfinal.dtos.paciente.PacienteEdit;
import com.utn.tpfinal.entities.Cama;
import com.utn.tpfinal.entities.HistoriaClinica;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.CamaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CamaRepository camaRepository;

    public PacienteService(PacienteRepository pacienteRepository, CamaRepository camaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.camaRepository = camaRepository;
    }

    // Alta del paciente: crea al paciente Y su historia clínica en la misma transacción
    @Transactional
    public PacienteDto crear(PacienteCreate dto) {
        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre().trim());
        paciente.setApellido(dto.getApellido().trim());
        paciente.setDiagnosticoBase(dto.getDiagnosticoBase());
        paciente.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
        paciente.setHistoriaClinica(new HistoriaClinica());

        return aDto(pacienteRepository.save(paciente), null);
    }

    @Transactional(readOnly = true)
    public List<PacienteDto> listar(boolean soloInternados) {
        List<Paciente> pacientes = soloInternados
                ? pacienteRepository.findByFechaAltaIsNullOrderByApellidoAscNombreAsc()
                : pacienteRepository.findAllByOrderByApellidoAscNombreAsc();

        // Una sola consulta para saber qué camas están ocupadas (evita una consulta por paciente)
        Map<Long, Cama> camaPorPaciente = new HashMap<>();
        for (Cama c : camaRepository.findByPacienteIsNotNull()) {
            camaPorPaciente.put(c.getPaciente().getId(), c);
        }

        return pacientes.stream().map(p -> aDto(p, camaPorPaciente.get(p.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public PacienteDto buscarPorId(Long id) {
        Paciente p = obtener(id);
        return aDto(p, camaRepository.findByPaciente_Id(id).orElse(null));
    }

    @Transactional
    public PacienteDto editar(Long id, PacienteEdit dto) {
        Paciente paciente = obtener(id);

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            paciente.setNombre(dto.getNombre().trim());
        }
        if (dto.getApellido() != null && !dto.getApellido().isBlank()) {
            paciente.setApellido(dto.getApellido().trim());
        }
        if (dto.getDiagnosticoBase() != null) {
            paciente.setDiagnosticoBase(dto.getDiagnosticoBase());
        }

        pacienteRepository.save(paciente);
        return aDto(paciente, camaRepository.findByPaciente_Id(id).orElse(null));
    }

    // Alta médica: registra la fecha de alta y libera la cama si tenía una.
    // Las dos cosas pasan en la misma transacción: nunca queda una cama "ocupada" por un paciente de alta.
    @Transactional
    public PacienteDto darDeAlta(Long id) {
        Paciente paciente = obtener(id);

        if (paciente.getFechaAlta() != null) {
            throw new ConflictoException("El paciente ya tiene el alta registrada");
        }
        paciente.setFechaAlta(LocalDate.now());
        pacienteRepository.save(paciente);

        camaRepository.findByPaciente_Id(id).ifPresent(cama -> {
            cama.setPaciente(null);
            cama.setEstado(EstadoCama.DISPONIBLE);
            camaRepository.save(cama);
        });

        return aDto(paciente, null);
    }

    // No hay "eliminar": una historia clínica es un registro legal que no se borra.

    private Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + id));
    }

    private PacienteDto aDto(Paciente p, Cama cama) {
        return new PacienteDto(p.getId(), p.getNombre(), p.getApellido(), p.getDiagnosticoBase(),
                p.getHistoriaClinica().getId(), p.getFechaIngreso(), p.getFechaAlta(),
                cama != null ? cama.getId() : null,
                cama != null ? cama.getNumero() : null);
    }
}
