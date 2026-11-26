package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Alimento> findByNombreIgnoreCase(String nombre);

    List<Alimento> findAllByOrderByNombreAsc();
}
