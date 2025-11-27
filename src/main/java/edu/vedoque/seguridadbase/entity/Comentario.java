package edu.vedoque.seguridadbase.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "text")
    private String contenido;

    @ManyToOne
    Noticia noticia;

    Date fecha;
}
