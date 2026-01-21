package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "pf_animales")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo;
    private String raza;
    private String imagenUrl;

    // Campos extra
    private int edad;
    private String localizacion;
    private String sexo;
    private boolean castrado;

    // --- RELACIÓN: Dueño del animal ---
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeGustaAnimal> likes = new ArrayList<>();
}