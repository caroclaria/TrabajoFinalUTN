package com.utn.tpfinal.repositories;
import com.utn.tpfinal.entities.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Long>{

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<Alimento> findByNombreIgnoreCase(String nombre);
}
