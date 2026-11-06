package com.utn.tpfinal.entities;
import com.utn.tpfinal.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")

// Las clses que hereden de Usuario, van a vivir el tablas seapardas:
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;
}