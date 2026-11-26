package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Evolucion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolucionRepository extends JpaRepository<Evolucion, Long> {

    // "historiaClinica_Id" navega la relación: evolucion.historiaClinica.id
    List<Evolucion> findByHistoriaClinica_IdOrderByFechaHoraAsc(Long historiaClinicaId);
}
