package com.utn.tpfinal.entities;

import com.utn.tpfinal.enums.TipoRegimen;
import com.utn.tpfinal.enums.TurnoComida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Menú cíclico: para cada "lista" (día del ciclo), régimen y turno hay un único menú.
// Ej: Lista 3 - HIPOSODICO - ALMUERZO -> [Pollo al horno sin sal, Puré de calabaza, Gelatina]
@Entity
@Table(name = "menu",
        uniqueConstraints = @UniqueConstraint(columnNames = {"numero_lista", "tipo_regimen", "turno"}))
@Getter
@Setter
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lista", nullable = false)
    private Integer numeroLista; // 1 a 10: día del ciclo

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_regimen", nullable = false, length = 30)
    private TipoRegimen tipoRegimen;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 30)
    private TurnoComida turnoComida;

    // N:M -> un plato puede repetirse en varios menús
    @ManyToMany
    @JoinTable(
            name = "menu_plato",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "plato_id")
    )
    private List<Plato> platos = new ArrayList<>();

    public Menu(Integer numeroLista, TipoRegimen tipoRegimen, TurnoComida turnoComida, List<Plato> platos) {
        this.numeroLista = numeroLista;
        this.tipoRegimen = tipoRegimen;
        this.turnoComida = turnoComida;
        this.platos = platos;
    }
}
