package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "nutricionista")

// Usa como id/PK el mismo valor que tiene el Usuario 
// del que vino y llama a esa columna usuario_id:
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)

// Hereda de Usuario 
public class Nutricionista extends Usuario {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;
}