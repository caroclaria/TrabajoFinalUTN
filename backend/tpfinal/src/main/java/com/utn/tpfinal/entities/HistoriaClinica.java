package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Cada paciente tiene exactamente una historia clínica. La tabla solo tiene el id:
// las evoluciones apuntan a ella (Evolucion.historiaClinica).
@Entity
@Table(name = "historia_clinica")
@Getter
@Setter
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
