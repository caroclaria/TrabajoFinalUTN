package com.utn.tpfinal.entities;

import com.utn.tpfinal.enums.TipoPreferencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Preferencia de un paciente sobre un alimento (RF04): Aceptado, Rechazo, Alergia o Intolerancia.
// Un paciente tiene como máximo UNA preferencia por alimento (UNIQUE paciente_id + alimento_id).
@Entity
@Table(name = "preferencia_alimentaria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"paciente_id", "alimento_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PreferenciaAlimentaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPreferencia tipo;

    public PreferenciaAlimentaria(Paciente paciente, Alimento alimento, TipoPreferencia tipo) {
        this.paciente = paciente;
        this.alimento = alimento;
        this.tipo = tipo;
    }
}
