package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.evolucion.EvolucionCreate;
import com.utn.tpfinal.dtos.evolucion.EvolucionDto;
import com.utn.tpfinal.entities.Evolucion;
import com.utn.tpfinal.entities.Nutricionista;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.EvolucionRepository;
import com.utn.tpfinal.repositories.NutricionistaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvolucionService {

    private final EvolucionRepository evolucionRepository;
    private final PacienteRepository pacienteRepository;
    private final NutricionistaRepository nutricionistaRepository;

    public EvolucionService(EvolucionRepository evolucionRepository,
                            PacienteRepository pacienteRepository,
                            NutricionistaRepository nutricionistaRepository) {
        this.evolucionRepository = evolucionRepository;
        this.pacienteRepository = pacienteRepository;
        this.nutricionistaRepository = nutricionistaRepository;
    }

    // Consulta cronológica (de la más antigua a la más reciente)
    @Transactional(readOnly = true)
    public List<EvolucionDto> listar(Long pacienteId) {
        Paciente paciente = obtenerPaciente(pacienteId);
        return evolucionRepository
                .findByHistoriaClinica_IdOrderByFechaHoraAsc(paciente.getHistoriaClinica().getId())
                .stream().map(this::aDto).toList();
    }

    // El nutricionista NO viene en el body: se toma del token (usuario autenticado).
    // Así nadie puede firmar una evolución a nombre de otro profesional.
    @Transactional
    public EvolucionDto crear(Long pacienteId, EvolucionCreate dto, String usernameAutenticado) {
        Paciente paciente = obtenerPaciente(pacienteId);

        if (paciente.getFechaAlta() != null) {
            throw new ConflictoException("El paciente tiene el alta registrada: no se pueden cargar evoluciones nuevas");
        }

        Nutricionista nutricionista = nutricionistaRepository.findByUsername(usernameAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario autenticado no tiene perfil de nutricionista"));

        Evolucion evolucion = new Evolucion();
        evolucion.setHistoriaClinica(paciente.getHistoriaClinica());
        evolucion.setNutricionista(nutricionista);
        evolucion.setNota(dto.getNota().trim());
        evolucion.setFechaHora(LocalDateTime.now());   // marca temporal puesta por el servidor

        return aDto(evolucionRepository.save(evolucion));
    }

    // No hay editar ni eliminar: las evoluciones son inmutables.

    private Paciente obtenerPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + id));
    }

    private EvolucionDto aDto(Evolucion e) {
        Nutricionista n = e.getNutricionista();
        return new EvolucionDto(e.getId(), e.getFechaHora(), e.getNota(),
                n.getNombre(), n.getApellido(), n.getMatricula());
    }
}
