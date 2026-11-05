package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String nombre;

    @ElementCollection
    @CollectionTable(
            name = "dish_ingredient",
            joinColumns = @JoinColumn(name = "dish_id")
    )
    private List<IngredientePlato> ingredientes = new ArrayList<>();

    public Plato(String nombre, List<IngredientePlato> ingredientes){
        this.nombre = nombre;
        this.ingredientes= ingredientes;
    }
}
