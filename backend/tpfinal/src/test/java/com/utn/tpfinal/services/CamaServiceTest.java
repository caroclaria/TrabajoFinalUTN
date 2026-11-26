package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.cama.CamaDto;
import com.utn.tpfinal.entities.Cama;
import com.utn.tpfinal.entities.HistoriaClinica;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.CamaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias de las reglas de asignación de camas (RF02).
// Los repositorios son "mocks": objetos falsos que simulan la base de datos, así que no hace falta conexión.
class CamaServiceTest {

    private CamaRepository camaRepository;
    private PacienteRepository pacienteRepository;
    private CamaService camaService;

    @BeforeEach
    void preparar() {
        camaRepository = mock(CamaRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        camaService = new CamaService(camaRepository, pacienteRepository);
        // save() devuelve el mismo objeto que recibe, como haría la base
        when(camaRepository.save(any(Cama.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Cama cama(Long id, EstadoCama estado) {
        Cama c = new Cama();
        c.setId(id);
        c.setNumero("101");
        c.setPiso(1);
        c.setSector(Sector.CLINICA);
        c.setEstado(estado);
        return c;
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

    // ---------- asignar ----------

    @Test
    void asignarPaciente_enCamaDisponible_laOcupa() {
        Cama cama = cama(1L, EstadoCama.DISPONIBLE);
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama));
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente(5L)));
        when(camaRepository.findByPaciente_Id(5L)).thenReturn(Optional.empty());

        CamaDto resultado = camaService.asignarPaciente(1L, 5L);

        assertEquals(EstadoCama.OCUPADA, resultado.getEstado());
        assertEquals(Long.valueOf(5L), resultado.getPacienteId());
        assertEquals("Gomez, Laura", resultado.getPacienteNombre());
        verify(camaRepository).save(cama);
    }

    @Test
    void asignarPaciente_enCamaOcupada_lanzaConflicto() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.OCUPADA)));

        assertThrows(ConflictoException.class, () -> camaService.asignarPaciente(1L, 5L));
        verify(camaRepository, never()).save(any(Cama.class));
    }

    @Test
    void asignarPaciente_enCamaEnMantenimiento_lanzaConflicto() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.MANTENIMIENTO)));

        assertThrows(ConflictoException.class, () -> camaService.asignarPaciente(1L, 5L));
        verify(camaRepository, never()).save(any(Cama.class));
    }

    @Test
    void asignarPaciente_conAltaRegistrada_lanzaConflicto() {
        Paciente conAlta = paciente(5L);
        conAlta.setFechaAlta(LocalDate.now());
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(conAlta));

        assertThrows(ConflictoException.class, () -> camaService.asignarPaciente(1L, 5L));
        verify(camaRepository, never()).save(any(Cama.class));
    }

    @Test
    void asignarPaciente_queYaTieneOtraCama_lanzaConflicto() {
        Cama otra = cama(2L, EstadoCama.OCUPADA);
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente(5L)));
        when(camaRepository.findByPaciente_Id(5L)).thenReturn(Optional.of(otra));

        assertThrows(ConflictoException.class, () -> camaService.asignarPaciente(1L, 5L));
        verify(camaRepository, never()).save(any(Cama.class));
    }

    @Test
    void asignarPaciente_pacienteInexistente_lanzaNoEncontrado() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> camaService.asignarPaciente(1L, 99L));
    }

    @Test
    void asignarPaciente_camaInexistente_lanzaNoEncontrado() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> camaService.asignarPaciente(1L, 5L));
    }

    // ---------- desasignar ----------

    @Test
    void desasignarPaciente_liberaLaCama() {
        Cama ocupada = cama(1L, EstadoCama.OCUPADA);
        ocupada.setPaciente(paciente(5L));
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ocupada));

        CamaDto resultado = camaService.desasignarPaciente(1L);

        assertEquals(EstadoCama.DISPONIBLE, resultado.getEstado());
        assertNull(resultado.getPacienteId());
    }

    @Test
    void desasignarPaciente_enCamaSinPaciente_lanzaConflicto() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));

        assertThrows(ConflictoException.class, () -> camaService.desasignarPaciente(1L));
    }

    // ---------- cambio manual de estado ----------

    @Test
    void cambiarEstado_aMantenimiento_funcionaSiLaCamaEstaLibre() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));

        CamaDto resultado = camaService.cambiarEstado(1L, EstadoCama.MANTENIMIENTO);

        assertEquals(EstadoCama.MANTENIMIENTO, resultado.getEstado());
    }

    @Test
    void cambiarEstado_aOcupadaAMano_estaProhibido() {
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cama(1L, EstadoCama.DISPONIBLE)));

        assertThrows(IllegalArgumentException.class, () -> camaService.cambiarEstado(1L, EstadoCama.OCUPADA));
    }

    @Test
    void cambiarEstado_deCamaConPaciente_lanzaConflicto() {
        Cama ocupada = cama(1L, EstadoCama.OCUPADA);
        ocupada.setPaciente(paciente(5L));
        when(camaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(ocupada));

        assertThrows(ConflictoException.class, () -> camaService.cambiarEstado(1L, EstadoCama.BLOQUEADA));
    }

    // ---------- eliminar ----------

    @Test
    void eliminar_camaOcupada_lanzaConflicto() {
        Cama ocupada = cama(1L, EstadoCama.OCUPADA);
        ocupada.setPaciente(paciente(5L));
        when(camaRepository.findById(1L)).thenReturn(Optional.of(ocupada));

        assertThrows(ConflictoException.class, () -> camaService.eliminar(1L));
        verify(camaRepository, never()).deleteById(any(Long.class));
    }
}
