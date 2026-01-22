package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;
import java.util.List; // Import necesario si usas listas en el futuro

@Data
@Entity
@Table(name = "pf_noticias")
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Exclude
    private long id;

    private String titulo;

    @Column(columnDefinition = "text")
    private String contenido;

    private String imagen;

    private Date fecha;

    @ManyToOne
    private User autor;

    // --- NUEVO: Campo temporal para la vista ---
    @Transient // Esto le dice a JPA que NO cree una columna en la base de datos
    private boolean likedByCurrentUser;

    // Campo para contar likes (opcional, si lo quieres calcular o guardar)
    @Transient
    private int cantidadMegusta;
}