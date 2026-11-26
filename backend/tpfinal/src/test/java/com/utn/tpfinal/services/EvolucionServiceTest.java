package com.utn.tpfinal.services;

import com.utn.tpfinal.controllers.EvolucionController;
import com.utn.tpfinal.dtos.evolucion.EvolucionCreate;
import com.utn.tpfinal.dtos.evolucion.EvolucionDto;
import com.utn.tpfinal.entities.Evolucion;
import com.utn.tpfinal.entities.HistoriaClinica;
import com.utn.tpfinal.entities.Nutricionista;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.EvolucionRepository;
import com.utn.tpfinal.repositories.NutricionistaRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import org.hibernate.annotations.Immutable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias de las evoluciones de la historia clínica (RF03): firma automática e inmutabilidad.
class EvolucionServiceTest {

    private EvolucionRepository evolucionRepository;
    private PacienteRepository pacienteRepository;
    private NutricionistaRepository nutricionistaRepository;
    private EvolucionService evolucionService;

    private HistoriaClinica historia;

    @BeforeEach
    void preparar() {
        evolucionRepository = mock(EvolucionRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        nutricionistaRepository = mock(NutricionistaRepository.class);
        evolucionService = new EvolucionService(evolucionRepository, pacienteRepository, nutricionistaRepository);
        when(evolucionRepository.save(any(Evolucion.class))).thenAnswer(inv -> inv.getArgument(0));

        historia = new HistoriaClinica();
        historia.setId(7L);
    }

    private Paciente paciente(Long id) {
        Paciente p = new Paciente();
        p.setId(id);
        p.setNombre("Laura");
        p.setApellido("Gomez");
        p.setFechaIngreso(LocalDate.now());
        p.setHistoriaClinica(historia);
        return p;
    }

    private Nutricionista nutricionista() {
        Nutricionista n = new Nutricionista();
        n.setUsername("ana");
        n.setNombre("Ana");
        n.setApellido("Perez");
        n.setMatricula("4521");
        return n;
    }

    private EvolucionCreate pedido(String nota) {
        EvolucionCreate dto = new EvolucionCreate();
        dto.setNota(nota);
        return dto;
    }

    @Test
    void crear_firmaConElNutricionistaDelTokenYPonePorSiMismaLaFecha() {
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente(5L)));
        when(nutricionistaRepository.findByUsername("ana")).thenReturn(Optional.of(nutricionista()));

        EvolucionDto resultado = evolucionService.crear(5L, pedido("  Tolera bien la dieta  "), "ana");

        assertEquals("Tolera bien la dieta", resultado.getNota());
        assertEquals("Perez", resultado.getNutricionistaApellido());
        assertEquals("4521", resultado.getNutricionistaMatricula());
        assertNotNull(resultado.getFechaHora());

        ArgumentCaptor<Evolucion> guardada = ArgumentCaptor.forClass(Evolucion.class);
        verify(evolucionRepository).save(guardada.capture());
        assertSame(historia, guardada.getValue().getHistoriaClinica());
    }

    @Test
    void crear_enPacienteConAlta_lanzaConflicto() {
        Paciente conAlta = paciente(5L);
        conAlta.setFechaAlta(LocalDate.now());
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(conAlta));

        assertThrows(ConflictoException.class, () -> evolucionService.crear(5L, pedido("Nota"), "ana"));
        verify(evolucionRepository, never()).save(any(Evolucion.class));
    }

    @Test
    void crear_conUsuarioQueNoEsNutricionista_lanzaNoEncontrado() {
        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente(5L)));
        when(nutricionistaRepository.findByUsername("admin")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> evolucionService.crear(5L, pedido("Nota"), "admin"));
        verify(evolucionRepository, never()).save(any(Evolucion.class));
    }

    @Test
    void crear_enPacienteInexistente_lanzaNoEncontrado() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> evolucionService.crear(99L, pedido("Nota"), "ana"));
    }

    @Test
    void listar_devuelveLasEvolucionesDeLaHistoriaDelPaciente() {
        Evolucion primera = new Evolucion();
        primera.setId(1L);
        primera.setNota("Ingreso");
        primera.setFechaHora(LocalDateTime.of(2026, 11, 3, 18, 40));
        primera.setNutricionista(nutricionista());

        Evolucion segunda = new Evolucion();
        segunda.setId(2L);
        segunda.setNota("Control");
        segunda.setFechaHora(LocalDateTime.of(2026, 11, 4, 9, 15));
        segunda.setNutricionista(nutricionista());

        when(pacienteRepository.findById(5L)).thenReturn(Optional.of(paciente(5L)));
        when(evolucionRepository.findByHistoriaClinica_IdOrderByFechaHoraAsc(7L)).thenReturn(List.of(primera, segunda));

        List<EvolucionDto> resultado = evolucionService.listar(5L);

        assertEquals(2, resultado.size());
        assertEquals("Ingreso", resultado.get(0).getNota());
        assertEquals("Control", resultado.get(1).getNota());
    }

    // ---------- inmutabilidad ----------

    @Test
    void laEntidadEvolucion_estaMarcadaComoInmutable() {
        assertTrue(Evolucion.class.isAnnotationPresent(Immutable.class));
    }

    @Test
    void elControlador_noOfreceEdicionNiBorradoDeEvoluciones() {
        for (Method metodo : EvolucionController.class.getDeclaredMethods()) {
            assertFalse(metodo.isAnnotationPresent(PutMapping.class), "No debe haber PUT: " + metodo.getName());
            assertFalse(metodo.isAnnotationPresent(PatchMapping.class), "No debe haber PATCH: " + metodo.getName());
            assertFalse(metodo.isAnnotationPresent(DeleteMapping.class), "No debe haber DELETE: " + metodo.getName());
        }
    }
}
