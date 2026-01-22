package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Data
@Entity
@Table(name = "pf_comentarios")
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "text")
    private String contenido;

    @ManyToOne
    private Noticia noticia;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    private Date fecha;
}