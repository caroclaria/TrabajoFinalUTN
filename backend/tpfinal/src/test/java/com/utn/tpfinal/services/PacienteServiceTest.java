package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.paciente.PacienteCreate;
import com.utn.tpfinal.dtos.paciente.PacienteDto;
import com.utn.tpfinal.entities.Cama;
import com.utn.tpfinal.entities.HistoriaClinica;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.repositories.CamaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias del alta de pacientes y del alta médica (RF03).
class PacienteServiceTest {

    private PacienteRepository pacienteRepository;
    private CamaRepository camaRepository;
    private PacienteService pacienteService;

    @BeforeEach
    void preparar() {
        pacienteRepository = mock(PacienteRepository.class);
        camaRepository = mock(CamaRepository.class);
        pacienteService = new PacienteService(pacienteRepository, camaRepository);
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Paciente paciente(Long id) {
        Paciente p = new Paciente();
        p.setId(id);
        p.setNombre("Laura");
        p.setApellido("Gomez");
        p.setFechaIngreso(LocalDate.now());
        p.setHistoriaClinica(new HistoriaClinica());
        return p;
    }

    @Test
    void crear_creaElPacienteConSuHistoriaClinicaYFechaDeIngresoDeHoy() {
        PacienteCreate pedido = new PacienteCreate();
        pedido.setNombre("  Laura ");
        pedido.setApellido(" Gomez ");
        pedido.setDiagnosticoBase("Diabetes tipo 2");

        PacienteDto resultado = pacienteService.crear(pedido);

        assertEquals("Laura", resultado.getNombre());
        assertEquals("Gomez", resultado.getApellido());
        assertEquals(LocalDate.now(), resultado.getFechaIngreso());

        ArgumentCaptor<Paciente> guardado = ArgumentCaptor.forClass(Paciente.class);
        verify(pacienteRepository).save(guardado.capture());
        assertNotNull(guardado.getValue().getHistoriaClinica());
    }

    @Test
    void darDeAlta_registraLaFechaYLiberaLaCama() {
        Paciente paciente = paciente(5L);
        Cama cama = new Cama();
        cama.setId(1L);
        cama.setNumero("101");
        cama.setPiso(1);
        cama.setSector(Sector.CLINICA);
        cama.setEstado(EstadoCama.OCUPADA);
        cama.setPaciente(paciente);

        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente));
        when(camaRepository.findByPaciente_Id(5L)).thenReturn(Optional.of(cama));

        PacienteDto resultado = pacienteService.darDeAlta(5L);

        assertEquals(LocalDate.now(), resultado.getFechaAlta());
        assertEquals(EstadoCama.DISPONIBLE, cama.getEstado());
        assertNull(cama.getPaciente());
        verify(camaRepository).save(cama);
    }

    @Test
    void darDeAlta_dosVeces_lanzaConflicto() {
        Paciente paciente = paciente(5L);
        paciente.setFechaAlta(LocalDate.now());
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente));

        assertThrows(ConflictoException.class, () -> pacienteService.darDeAlta(5L));
    }
}
