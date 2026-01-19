package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "pf_noticias")
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String titulo;
    @Column(columnDefinition = "text")
    private String contenido;
    private String imagen;
    private Date fecha;
    @ManyToOne
    private User autor;
}
