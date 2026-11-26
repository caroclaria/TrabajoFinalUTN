package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Cama;
import com.utn.tpfinal.enums.Sector;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CamaRepository extends JpaRepository<Cama, Long> {

    Optional<Cama> findByNumero(String numero);

    // Muestra las camas agrupadas por piso y sector, con su estado.
    List<Cama> findByPisoAndSector(Integer piso, Sector sector);

    // Todas las camas ordenadas para armar el panel (piso, sector, número)
    List<Cama> findAllByOrderByPisoAscSectorAscNumeroAsc();

    // Cama que ocupa un paciente (si tiene alguna)
    Optional<Cama> findByPaciente_Id(Long pacienteId);

    // Camas que tienen un paciente asignado
    List<Cama> findByPacienteIsNotNull();

    // Trae la cama BLOQUEANDO la fila hasta que termine la transacción (SELECT ... FOR UPDATE).
    // Si dos usuarios intentan asignar la misma cama a la vez, el segundo espera al primero y
    // al leerla ya la ve ocupada: así se evitan solapamientos (RNF01).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cama c where c.id = :id")
    Optional<Cama> findByIdForUpdate(@Param("id") Long id);
}
