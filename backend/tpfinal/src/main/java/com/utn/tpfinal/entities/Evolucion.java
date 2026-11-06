package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

// Registro inmutable (RF03). @Immutable le dice a Hibernate que una vez insertada la fila
// nunca la actualice: aunque alguien modifique el objeto en memoria, no se genera UPDATE.
// Además, no existe ningún endpoint PUT/PATCH/DELETE para evoluciones.
@Entity
@Table(name = "evolucion")
@Immutable
@Getter
@Setter
public class Evolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_clinica_id", nullable = false, updatable = false)
    private HistoriaClinica historiaClinica;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String nota;

    // La "firma" del nutricionista (nombre + apellido + matrícula salen de esta relación)
    @ManyToOne(optional = false)
    @JoinColumn(name = "nutricionista_id", nullable = false, updatable = false)
    private Nutricionista nutricionista;
}
