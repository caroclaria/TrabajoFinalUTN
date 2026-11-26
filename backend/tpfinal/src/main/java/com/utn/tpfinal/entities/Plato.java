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

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    // Ingredientes del plato: se guardan en la tabla plato_ingrediente.
    // Es la base del motor de matching (RF05): se cruzan contra las preferencias del paciente.
    @ElementCollection
    @CollectionTable(name = "plato_ingrediente", joinColumns = @JoinColumn(name = "plato_id"))
    private List<IngredientePlato> ingredientes = new ArrayList<>();

    public Plato(String nombre, List<IngredientePlato> ingredientes) {
        this.nombre = nombre;
        this.ingredientes = ingredientes;
    }
}
