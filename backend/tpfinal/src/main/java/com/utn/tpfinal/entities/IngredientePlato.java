package com.utn.tpfinal.entities;


import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientePlato {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Alimento alimento;

    @Column(name = "quantity", nullable = false)
    private Double cantidad;

    @Column(name = "unit_of_measure", length = 30)
    private String unidadMedida;
}
