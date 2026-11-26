package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.response.MatchingResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoEvaluadoDTO;
import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.IngredientePlato;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Paciente;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import com.utn.tpfinal.enums.SeveridadConflicto;
import com.utn.tpfinal.enums.TipoPreferencia;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PacienteRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias del motor de matching (RF05).
class MatchingServiceTest {

    private PacienteRepository pacienteRepository;
    private MenuRepository menuRepository;
    private PreferenciaAlimentariaRepository preferenciaRepository;
    private MatchingService matchingService;

    private final Alimento huevo = alimento(1L, "Huevo");
    private final Alimento tomate = alimento(2L, "Tomate");
    private final Alimento lechuga = alimento(3L, "Lechuga");
    private final Alimento pollo = alimento(4L, "Pollo");
    private final Alimento leche = alimento(5L, "Leche");

    private final Plato milanesa = plato(10L, "Milanesa", huevo, leche);
    private final Plato ensaladaMixta = plato(11L, "Ensalada mixta", lechuga, tomate);
    private final Plato polloAlHorno = plato(12L, "Pollo al horno", pollo);
    private final Plato flan = plato(13L, "Flan", huevo, leche);

    private Paciente paciente;

    @BeforeEach
    void preparar() {
        pacienteRepository = mock(PacienteRepository.class);
        menuRepository = mock(MenuRepository.class);
        preferenciaRepository = mock(PreferenciaAlimentariaRepository.class);
        matchingService = new MatchingService(pacienteRepository, menuRepository, preferenciaRepository);

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setTipoRegimen(TipoRegimen.GENERAL);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
    }

    @Test
    void menuSinConflictos_esApto() {
        Menu menu = menu(100L, polloAlHorno, ensaladaMixta);
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));
        preferencias(pref(huevo, TipoPreferencia.ALERGIA));

        MatchingResponseDTO r = matchingService.evaluarMenu(1L, 100L);

        assertTrue(r.apto());
        assertEquals(SeveridadConflicto.NINGUNA, r.severidad());
        assertTrue(r.platos().stream().allMatch(PlatoEvaluadoDTO::apto));
        // Si no hay conflictos no se buscan reemplazos
        verify(menuRepository, never()).platosDelRegimenYTurno(TipoRegimen.GENERAL, TurnoComida.ALMUERZO);
    }

    @Test
    void alergiaEnUnIngrediente_marcaElPlatoComoCritico() {
        Menu menu = menu(100L, milanesa, polloAlHorno);
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));
        when(menuRepository.platosDelRegimenYTurno(TipoRegimen.GENERAL, TurnoComida.ALMUERZO))
                .thenReturn(List.of(milanesa, polloAlHorno));
        preferencias(pref(huevo, TipoPreferencia.ALERGIA));

        MatchingResponseDTO r = matchingService.evaluarMenu(1L, 100L);

        assertFalse(r.apto());
        assertEquals(SeveridadConflicto.CRITICA, r.severidad());

        PlatoEvaluadoDTO evaluadoMilanesa = r.platos().get(0);
        assertFalse(evaluadoMilanesa.apto());
        assertEquals(1, evaluadoMilanesa.conflictos().size());
        assertEquals("Huevo", evaluadoMilanesa.conflictos().get(0).alimentoNombre());
        assertEquals(TipoPreferencia.ALERGIA, evaluadoMilanesa.conflictos().get(0).tipo());

        assertTrue(r.platos().get(1).apto());
    }

    @Test
    void laSeveridadDelMenuEsLaDelConflictoMasGrave() {
        Menu menu = menu(100L, ensaladaMixta, milanesa);
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));
        when(menuRepository.platosDelRegimenYTurno(TipoRegimen.GENERAL, TurnoComida.ALMUERZO)).thenReturn(List.of());
        preferencias(pref(tomate, TipoPreferencia.RECHAZO), pref(leche, TipoPreferencia.INTOLERANCIA));

        MatchingResponseDTO r = matchingService.evaluarMenu(1L, 100L);

        assertEquals(SeveridadConflicto.BAJA, r.platos().get(0).severidad());   // ensalada: rechazo al tomate
        assertEquals(SeveridadConflicto.ALTA, r.platos().get(1).severidad());   // milanesa: intolerancia a la leche
        assertEquals(SeveridadConflicto.ALTA, r.severidad());
    }

    @Test
    void preferenciaAceptado_noGeneraConflicto() {
        Menu menu = menu(100L, milanesa);
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));
        preferencias(pref(huevo, TipoPreferencia.ACEPTADO));

        assertTrue(matchingService.evaluarMenu(1L, 100L).apto());
    }

    @Test
    void sugiereReemplazosSinConflictos_yQueNoEstenYaEnElMenu() {
        Menu menu = menu(100L, milanesa, ensaladaMixta);
        when(menuRepository.findById(100L)).thenReturn(Optional.of(menu));
        // Candidatos del mismo régimen y turno: el flan también tiene huevo, la ensalada ya está en el menú
        when(menuRepository.platosDelRegimenYTurno(TipoRegimen.GENERAL, TurnoComida.ALMUERZO))
                .thenReturn(List.of(flan, ensaladaMixta, polloAlHorno));
        preferencias(pref(huevo, TipoPreferencia.ALERGIA));

        PlatoEvaluadoDTO evaluadoMilanesa = matchingService.evaluarMenu(1L, 100L).platos().get(0);

        assertEquals(1, evaluadoMilanesa.reemplazos().size());
        assertEquals("Pollo al horno", evaluadoMilanesa.reemplazos().get(0).nombre());
    }

    @Test
    void menuAsignado_usaElRegimenDelPaciente() {
        Menu menu = menu(100L, polloAlHorno);
        when(menuRepository.findByNumeroListaAndTipoRegimenAndTurnoComida(3, TipoRegimen.GENERAL, TurnoComida.ALMUERZO))
                .thenReturn(Optional.of(menu));
        preferencias();

        MatchingResponseDTO r = matchingService.evaluarMenuAsignado(1L, 3, TurnoComida.ALMUERZO);

        assertEquals(100L, r.menuId());
        assertTrue(r.apto());
    }

    @Test
    void menuAsignado_sinRegimenIndicado_lanzaConflicto() {
        paciente.setTipoRegimen(null);

        assertThrows(ConflictoException.class,
                () -> matchingService.evaluarMenuAsignado(1L, 1, TurnoComida.ALMUERZO));
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private void preferencias(PreferenciaAlimentaria... prefs) {
        when(preferenciaRepository.findByPacienteId(1L)).thenReturn(List.of(prefs));
    }

    private PreferenciaAlimentaria pref(Alimento a, TipoPreferencia tipo) {
        return new PreferenciaAlimentaria(paciente, a, tipo);
    }

    private static Alimento alimento(Long id, String nombre) {
        Alimento a = new Alimento(nombre);
        a.setId(id);
        return a;
    }

    private static Plato plato(Long id, String nombre, Alimento... alimentos) {
        List<IngredientePlato> ingredientes = new ArrayList<>();
        for (Alimento a : alimentos) {
            ingredientes.add(new IngredientePlato(a, 100.0, "g"));
        }
        Plato p = new Plato(nombre, ingredientes);
        p.setId(id);
        return p;
    }

    private static Menu menu(Long id, Plato... platos) {
        Menu m = new Menu(1, TipoRegimen.GENERAL, TurnoComida.ALMUERZO, new ArrayList<>(List.of(platos)));
        m.setId(id);
        return m;
    }
}
