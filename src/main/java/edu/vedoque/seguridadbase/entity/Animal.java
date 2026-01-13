package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data; // Si usas Lombok. Si no, genera Getters y Setters a mano.

@Entity
@Data // Genera getters, setters y toString automáticamente
@Table(name = "animales")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    // Nombre del archivo de imagen (ej: "perro1.jpg")
    private String imagen;

    // Campos para filtros
    private String tipo;    // PERRO, GATO, OTRO
    private String raza;    // Pastor Alemán, Siamés...
    private int edad;       // En años
    private String sexo;    // MACHO, HEMBRA
    private String ciudad;  // Madrid, Valencia...
    private boolean castrado;
    private String color;
}