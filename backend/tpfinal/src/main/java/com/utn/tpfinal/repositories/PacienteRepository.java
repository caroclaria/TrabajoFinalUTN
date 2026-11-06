package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Pacientes que siguen internados (sin fecha de alta), ordenados por apellido
    List<Paciente> findByFechaAltaIsNullOrderByApellidoAscNombreAsc();

    List<Paciente> findAllByOrderByApellidoAscNombreAsc();
}
