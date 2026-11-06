package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.cama.CamaCreate;
import com.utn.tpfinal.dtos.cama.CamaDto;
import com.utn.tpfinal.dtos.cama.CamaEdit;
import com.utn.tpfinal.entities.Cama;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.CamaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CamaService {

    private final CamaRepository camaRepository;
    private final PacienteRepository pacienteRepository;

    public CamaService(CamaRepository camaRepository, PacienteRepository pacienteRepository) {
        this.camaRepository = camaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public CamaDto crear(CamaCreate dto) {
        if (camaRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cama con ese número");
        }

        Cama cama = new Cama();
        cama.setNumero(dto.getNumero());
        cama.setPiso(dto.getPiso());
        cama.setSector(dto.getSector());

        return aDto(camaRepository.save(cama));
    }

    @Transactional(readOnly = true)
    public List<CamaDto> listarTodos() {
        return camaRepository.findAllByOrderByPisoAscSectorAscNumeroAsc().stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CamaDto> listarPorPisoYSector(Integer piso, Sector sector) {
        return camaRepository.findByPisoAndSector(piso, sector).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public CamaDto buscarPorId(Long id) {
        return aDto(obtener(id));
    }

    public CamaDto editar(Long id, CamaEdit dto) {
        Cama cama = obtener(id);

        if (dto.getNumero() != null && !dto.getNumero().isBlank()
                && !dto.getNumero().equals(cama.getNumero())) {
            if (camaRepository.findByNumero(dto.getNumero()).isPresent()) {
                throw new IllegalArgumentException("Ya existe una cama con ese número");
            }
            cama.setNumero(dto.getNumero());
        }
        if (dto.getPiso() != null) {
            cama.setPiso(dto.getPiso());
        }
        if (dto.getSector() != null) {
            cama.setSector(dto.getSector());
        }

        return aDto(camaRepository.save(cama));
    }

    // Cambio manual de estado (Disponible / Mantenimiento / Bloqueada).
    // "Ocupada" NO se puede poner a mano: la cama pasa a ese estado solo al asignarle un paciente.
    @Transactional
    public CamaDto cambiarEstado(Long id, EstadoCama nuevoEstado) {
        Cama cama = obtenerParaActualizar(id);

        if (nuevoEstado == EstadoCama.OCUPADA) {
            throw new IllegalArgumentException("Una cama pasa a OCUPADA al asignarle un paciente");
        }
        if (cama.getPaciente() != null) {
            throw new ConflictoException("La cama tiene un paciente asignado: desasignalo antes de cambiar su estado");
        }

        cama.setEstado(nuevoEstado);
        return aDto(camaRepository.save(cama));
    }

    // Asigna un paciente a una cama. Todo ocurre en una sola transacción (RNF01):
    // o se cumplen todas las validaciones y se guarda, o no se guarda nada.
    @Transactional
    public CamaDto asignarPaciente(Long camaId, Long pacienteId) {
        // Bloquea la fila de la cama: si otra persona la está asignando al mismo tiempo, espera
        Cama cama = obtenerParaActualizar(camaId);

        if (cama.getEstado() != EstadoCama.DISPONIBLE || cama.getPaciente() != null) {
            throw new ConflictoException("La cama no está disponible (estado actual: " + cama.getEstado() + ")");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + pacienteId));

        if (paciente.getFechaAlta() != null) {
            throw new ConflictoException("El paciente ya tiene el alta registrada");
        }
        camaRepository.findByPaciente_Id(pacienteId).ifPresent(otra -> {
            throw new ConflictoException("El paciente ya ocupa la cama " + otra.getNumero()
                    + ": desasignalo antes de asignarle otra");
        });

        cama.setPaciente(paciente);
        cama.setEstado(EstadoCama.OCUPADA);
        return aDto(camaRepository.save(cama));
    }

    @Transactional
    public CamaDto desasignarPaciente(Long camaId) {
        Cama cama = obtenerParaActualizar(camaId);

        if (cama.getPaciente() == null) {
            throw new ConflictoException("La cama no tiene ningún paciente asignado");
        }

        cama.setPaciente(null);
        cama.setEstado(EstadoCama.DISPONIBLE);
        return aDto(camaRepository.save(cama));
    }

    public void eliminar(Long id) {
        Cama cama = obtener(id);
        if (cama.getPaciente() != null) {
            throw new ConflictoException("No se puede eliminar una cama ocupada");
        }
        camaRepository.deleteById(id);
    }

    private Cama obtener(Long id) {
        return camaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cama no encontrada con id " + id));
    }

    // Igual que obtener(), pero bloqueando la fila hasta el final de la transacción
    private Cama obtenerParaActualizar(Long id) {
        return camaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cama no encontrada con id " + id));
    }

    private CamaDto aDto(Cama c) {
        Paciente p = c.getPaciente();
        return new CamaDto(c.getId(), c.getNumero(), c.getPiso(), c.getSector(), c.getEstado(),
                p != null ? p.getId() : null,
                p != null ? p.getApellido() + ", " + p.getNombre() : null);
    }
}
