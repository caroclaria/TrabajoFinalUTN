package com.utn.tpfinal.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alimento")
@Getter
@Setter
@NoArgsConstructor
public class Alimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    public Alimento (String nombre){
        this.nombre = nombre;
    }
}
