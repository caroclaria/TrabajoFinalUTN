package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.MenuRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.MenuResponseDTO;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import com.utn.tpfinal.exceptions.ConflictoException;
import com.utn.tpfinal.exceptions.RecursoNoEncontradoException;
import com.utn.tpfinal.repositories.MenuRepository;
import com.utn.tpfinal.repositories.PlatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Pruebas unitarias de la configuración de menús cíclicos (RF04).
class MenuServiceTest {

    private MenuRepository menuRepository;
    private PlatoRepository platoRepository;
    private MenuService menuService;

    @BeforeEach
    void preparar() {
        menuRepository = mock(MenuRepository.class);
        platoRepository = mock(PlatoRepository.class);
        menuService = new MenuService(menuRepository, platoRepository);
        when(menuRepository.save(any(Menu.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Plato plato(Long id, String nombre) {
        Plato p = new Plato(nombre, new ArrayList<>());
        p.setId(id);
        when(platoRepository.findById(id)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void crear_armaElMenuConLosPlatosIndicados() {
        plato(1L, "Pollo");
        plato(2L, "Puré");
        MenuRequestDTO pedido = new MenuRequestDTO(3, TipoRegimen.HIPOSODICO, TurnoComida.ALMUERZO, List.of(1L, 2L));

        MenuResponseDTO creado = menuService.crear(pedido);

        assertEquals(3, creado.numeroLista());
        assertEquals(TipoRegimen.HIPOSODICO, creado.tipoRegimen());
        assertEquals(2, creado.platos().size());
        assertEquals("Pollo", creado.platos().get(0).nombre());
    }

    @Test
    void crear_siYaExisteMenuParaListaRegimenYTurno_lanzaConflicto() {
        Menu existente = new Menu(3, TipoRegimen.HIPOSODICO, TurnoComida.ALMUERZO, new ArrayList<>());
        existente.setId(99L);
        when(menuRepository.findByNumeroListaAndTipoRegimenAndTurnoComida(3, TipoRegimen.HIPOSODICO, TurnoComida.ALMUERZO))
                .thenReturn(Optional.of(existente));

        assertThrows(ConflictoException.class, () -> menuService.crear(
                new MenuRequestDTO(3, TipoRegimen.HIPOSODICO, TurnoComida.ALMUERZO, List.of(1L))));
    }

    @Test
    void crear_conPlatoInexistente_lanzaNoEncontrado() {
        when(platoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> menuService.crear(
                new MenuRequestDTO(1, TipoRegimen.GENERAL, TurnoComida.CENA, List.of(5L))));
    }

    @Test
    void crear_conPlatoRepetido_lanzaSolicitudInvalida() {
        plato(1L, "Pollo");

        assertThrows(IllegalArgumentException.class, () -> menuService.crear(
                new MenuRequestDTO(1, TipoRegimen.GENERAL, TurnoComida.CENA, List.of(1L, 1L))));
    }

    @Test
    void actualizar_puedeConservarSuPropiaCombinacion() {
        Menu menu = new Menu(1, TipoRegimen.GENERAL, TurnoComida.CENA, new ArrayList<>());
        menu.setId(7L);
        when(menuRepository.findById(7L)).thenReturn(Optional.of(menu));
        when(menuRepository.findByNumeroListaAndTipoRegimenAndTurnoComida(1, TipoRegimen.GENERAL, TurnoComida.CENA))
                .thenReturn(Optional.of(menu));
        plato(2L, "Sopa");

        MenuResponseDTO r = menuService.actualizar(7L,
                new MenuRequestDTO(1, TipoRegimen.GENERAL, TurnoComida.CENA, List.of(2L)));

        assertEquals(1, r.platos().size());
        assertEquals("Sopa", r.platos().get(0).nombre());
    }
}
