package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MeGustaAnimal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;
}