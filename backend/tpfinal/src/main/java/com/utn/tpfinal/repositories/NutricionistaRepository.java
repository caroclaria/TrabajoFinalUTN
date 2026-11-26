package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Nutricionista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NutricionistaRepository extends JpaRepository<Nutricionista, Long> {

    Optional<Nutricionista> findByMatricula(String matricula);

    // username está definido en Usuario (la clase padre), Spring Data lo resuelve igual
    Optional<Nutricionista> findByUsername(String username);
}
