package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.PreferenciaAlimentaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PreferenciaAlimentariaRepository extends JpaRepository<PreferenciaAlimentaria, Long> {

    // Trae también el alimento en la misma consulta (evita N+1 al armar los DTOs)
    @Query("""
            select p from PreferenciaAlimentaria p join fetch p.alimento
            where p.paciente.id = :pacienteId
            order by p.alimento.nombre
            """)
    List<PreferenciaAlimentaria> findByPacienteId(@Param("pacienteId") Long pacienteId);

    Optional<PreferenciaAlimentaria> findByIdAndPaciente_Id(Long id, Long pacienteId);

    boolean existsByPaciente_IdAndAlimento_Id(Long pacienteId, Long alimentoId);

    boolean existsByAlimento_Id(Long alimentoId);
}
