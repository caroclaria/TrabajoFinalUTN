package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Data;
import com.utn.tpfinal.enums.EstadoCama;
import com.utn.tpfinal.enums.Sector;

@Entity
@Table(name = "cama")


@Data
public class Cama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false)
    private  int piso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sector sector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCama estado = EstadoCama.DISPONIBLE;

    // Paciente que ocupa la cama (null si no está ocupada). La columna paciente_id es UNIQUE
    // en la base: un paciente no puede estar en dos camas a la vez (RNF01).
    @OneToOne
    @JoinColumn(name = "paciente_id", unique = true)
    private Paciente paciente;
}
