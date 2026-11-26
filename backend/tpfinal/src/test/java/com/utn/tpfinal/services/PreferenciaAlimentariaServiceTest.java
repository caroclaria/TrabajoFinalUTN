package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.PreferenciaCreateDTO;
import com.utn.tpfinal.dtos.regimen.request.PreferenciaUpdateDTO;
import com.utn.tpfinal.dtos.regimen.response.PreferenciaResponseDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoPreferencia;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Pruebas unitarias del registro de preferencias alimentarias (RF04).
class PreferenciaAlimentariaServiceTest {

    private PreferenciaAlimentariaRepository preferenciaRepository;
    private PacienteRepository pacienteRepository;
    private AlimentoRepository alimentoRepository;
    private PreferenciaAlimentariaService service;

    private Paciente paciente;
    private Alimento mani;

    @BeforeEach
    void preparar() {
        preferenciaRepository = mock(PreferenciaAlimentariaRepository.class);
        pacienteRepository = mock(PacienteRepository.class);
        alimentoRepository = mock(AlimentoRepository.class);
        service = new PreferenciaAlimentariaService(preferenciaRepository, pacienteRepository, alimentoRepository);

        paciente = new Paciente();
        paciente.setId(1L);
        mani = new Alimento("Maní");
        mani.setId(8L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(alimentoRepository.findById(8L)).thenReturn(Optional.of(mani));
        when(preferenciaRepository.save(any(PreferenciaAlimentaria.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void crear_registraLaPreferenciaConSuSeveridad() {
        PreferenciaResponseDTO r = service.crear(1L, new PreferenciaCreateDTO(8L, TipoPreferencia.ALERGIA));

        assertEquals("Maní", r.alimentoNombre());
        assertEquals(TipoPreferencia.ALERGIA, r.tipo());
        assertEquals(SeveridadConflicto.CRITICA, r.severidad());
    }

    @Test
    void crear_siYaHayPreferenciaParaEseAlimento_lanzaConflicto() {
        when(preferenciaRepository.existsByPaciente_IdAndAlimento_Id(1L, 8L)).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> service.crear(1L, new PreferenciaCreateDTO(8L, TipoPreferencia.RECHAZO)));
    }

    @Test
    void crear_conPacienteInexistente_lanzaNoEncontrado() {
        when(pacienteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.crear(2L, new PreferenciaCreateDTO(8L, TipoPreferencia.RECHAZO)));
    }

    @Test
    void actualizar_cambiaElTipo() {
        PreferenciaAlimentaria pref = new PreferenciaAlimentaria(paciente, mani, TipoPreferencia.RECHAZO);
        pref.setId(3L);
        when(preferenciaRepository.findByIdAndPaciente_Id(3L, 1L)).thenReturn(Optional.of(pref));

        PreferenciaResponseDTO r = service.actualizar(1L, 3L, new PreferenciaUpdateDTO(TipoPreferencia.INTOLERANCIA));

        assertEquals(TipoPreferencia.INTOLERANCIA, r.tipo());
        assertEquals(SeveridadConflicto.ALTA, r.severidad());
    }

    @Test
    void actualizar_preferenciaDeOtroPaciente_lanzaNoEncontrado() {
        when(preferenciaRepository.findByIdAndPaciente_Id(3L, 1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.actualizar(1L, 3L, new PreferenciaUpdateDTO(TipoPreferencia.ALERGIA)));
    }
}
