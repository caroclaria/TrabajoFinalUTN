package com.utn.tpfinal.services;

import com.utn.tpfinal.entities.Alimento;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.repositories.AlimentoRepository;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import com.utn.tpfinal.repositories.PreferenciaAlimentariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas de que no se pueden borrar alimentos ni platos que están en uso (integridad del catálogo).
class CatalogoEliminacionTest {

    private AlimentoRepository alimentoRepository;
    private PlatoRepository platoRepository;
    private MenuRepository menuRepository;
    private PreferenciaAlimentariaRepository preferenciaRepository;
    private AlimentoService alimentoService;
    private PlatoService platoService;
    private Alimento huevo;

    @BeforeEach
    void preparar() {
        alimentoRepository = mock(AlimentoRepository.class);
        platoRepository = mock(PlatoRepository.class);
        menuRepository = mock(MenuRepository.class);
        preferenciaRepository = mock(PreferenciaAlimentariaRepository.class);
        alimentoService = new AlimentoService(alimentoRepository, platoRepository, preferenciaRepository);
        platoService = new PlatoService(platoRepository, alimentoRepository, menuRepository);

        huevo = new Alimento("Huevo");
        huevo.setId(1L);
        when(alimentoRepository.findById(1L)).thenReturn(Optional.of(huevo));

        Plato flan = new Plato("Flan", new ArrayList<>());
        flan.setId(10L);
        when(platoRepository.findById(10L)).thenReturn(Optional.of(flan));
    }

    @Test
    void eliminarAlimento_queEsIngredienteDeUnPlato_lanzaConflicto() {
        when(platoRepository.existsByIngredientes_Alimento_Id(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> alimentoService.eliminar(1L));
    }

    @Test
    void eliminarAlimento_conPreferenciasDePacientes_lanzaConflicto() {
        when(preferenciaRepository.existsByAlimento_Id(1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> alimentoService.eliminar(1L));
    }

    @Test
    void eliminarAlimento_sinUso_loBorra() {
        alimentoService.eliminar(1L);

        verify(alimentoRepository).delete(huevo);
    }

    @Test
    void eliminarPlato_queEstaEnUnMenu_lanzaConflicto() {
        when(menuRepository.existsByPlatos_Id(10L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> platoService.delete(10L));
    }
}
