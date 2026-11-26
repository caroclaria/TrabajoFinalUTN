package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // El menú que le corresponde a un régimen en un turno y día del ciclo (lo usan el matching y cocina)
    Optional<Menu> findByNumeroListaAndTipoRegimenAndTurnoComida(Integer numeroLista,
                                                                 TipoRegimen tipoRegimen,
                                                                 TurnoComida turnoComida);

    // Filtro opcional: si un parámetro llega null, no filtra por ese campo
    @Query("""
            select m from Menu m
            where (:numeroLista is null or m.numeroLista = :numeroLista)
              and (:tipoRegimen is null or m.tipoRegimen = :tipoRegimen)
              and (:turno is null or m.turnoComida = :turno)
            order by m.numeroLista, m.tipoRegimen, m.turnoComida
            """)
    List<Menu> buscar(@Param("numeroLista") Integer numeroLista,
                      @Param("tipoRegimen") TipoRegimen tipoRegimen,
                      @Param("turno") TurnoComida turno);

    // Platos que se sirven en algún menú del mismo régimen y turno: candidatos a reemplazo (RF05)
    @Query("""
            select distinct p from Menu m join m.platos p
            where m.tipoRegimen = :tipoRegimen and m.turnoComida = :turno
            order by p.nombre
            """)
    List<Plato> platosDelRegimenYTurno(@Param("tipoRegimen") TipoRegimen tipoRegimen,
                                       @Param("turno") TurnoComida turno);

    // ¿El plato está en algún menú? (para no borrar platos en uso)
    boolean existsByPlatos_Id(Long platoId);
}
