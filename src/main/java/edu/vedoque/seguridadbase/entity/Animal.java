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
    private String tipo; // "PERRO" o "GATO"
    private String raza;
    private String imagenUrl;

    // --- CAMPOS QUE FALTABAN ---
    private int edad;
    private String localizacion;
    private String sexo;
    private boolean castrado;
}