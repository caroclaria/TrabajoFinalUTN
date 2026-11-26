package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Plato> findByNombreIgnoreCase(String nombre);

    List<Plato> findAllByOrderByNombreAsc();

    // ¿Algún plato usa este alimento como ingrediente? (para no borrar alimentos en uso)
    boolean existsByIngredientes_Alimento_Id(Long alimentoId);
}
