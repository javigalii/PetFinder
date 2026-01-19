package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private int edad;
    private String localizacion;
    private String sexo;
    private boolean castrado;

    // --- NUEVO CAMPO: DUEÑO DEL ANIMAL ---
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
}