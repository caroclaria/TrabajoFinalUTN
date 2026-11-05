package com.utn.tpfinal.repositories;

import com.utn.tpfinal.entities.Menu;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {


}
