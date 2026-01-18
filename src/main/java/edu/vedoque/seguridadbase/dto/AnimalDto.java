package edu.vedoque.seguridadbase.dto;

import lombok.Data;

@Data
public class AnimalDto {
    private Long id;
    private String nombre;
    private String tipo;
    private String raza;
    private String imagenUrl;

    // --- CAMPOS QUE FALTABAN ---
    private int edad;
    private String localizacion;
    private String sexo;
    private boolean castrado;

    // El campo especial para el corazón
    private boolean likedByCurrentUser;
}