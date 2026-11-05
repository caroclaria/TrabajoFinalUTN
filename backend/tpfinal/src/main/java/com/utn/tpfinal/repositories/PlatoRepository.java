package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Plato;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatoRepository extends JpaRepository<Plato, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Plato> findByNombreIgnoreCase(String nombre);
}
