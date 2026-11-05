package com.utn.tpfinal.entities;

import com.utn.tpfinal.enums.TurnoComida;
import jakarta.persistence.*;
import lombok.*;
import com.utn.tpfinal.enums.TipoRegimen;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "list_number", nullable = false)
    private Integer numeroLista; // 1 a 10 (La "Lista" o día del ciclo)

    @Enumerated(EnumType.STRING)
    @Column(name = "regimen_type", nullable = false)
    private TipoRegimen tipoRegimen; // GENERAL, HIPOSODICO, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_shift", nullable = false)
    private TurnoComida turnoComida; // ALMUERZO, CENA, etc.

    @ManyToMany
    @JoinTable(
            name = "menu_plato",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "plato_id")
    )
    private List<Plato> platos = new ArrayList<>();

    public Menu(Integer numeroLista, TipoRegimen tipoRegimen,  TurnoComida turnoComida, List<Plato> platos ) {
        this.numeroLista = numeroLista;
        this.tipoRegimen = tipoRegimen;
        this.turnoComida = turnoComida;
        this.platos = platos;

    }

}
