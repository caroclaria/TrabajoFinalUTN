package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "paciente")
@Getter
@Setter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    // Diagnóstico médico de base (RF03)
    @Column(name = "diagnostico_base", columnDefinition = "TEXT")
    private String diagnosticoBase;

    // 1 a 1: al guardar el paciente por primera vez, JPA guarda también su historia clínica
    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "historia_clinica_id", nullable = false, unique = true)
    private HistoriaClinica historiaClinica;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    // null mientras el paciente sigue internado
    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;
}
