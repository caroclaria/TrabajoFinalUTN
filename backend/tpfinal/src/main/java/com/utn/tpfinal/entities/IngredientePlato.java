package com.utn.tpfinal.entities;

import jakarta.persistence.*;
import lombok.*;

// Ingrediente de un plato: qué alimento lleva y en qué cantidad.
// Es @Embeddable porque no tiene vida propia: existe solo como parte de un Plato.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientePlato {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    @Column(name = "unidad_medida", length = 30)
    private String unidadMedida;
}
