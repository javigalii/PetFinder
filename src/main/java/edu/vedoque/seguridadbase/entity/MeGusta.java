package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MeGusta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    User usuario;

    @ManyToOne
    Noticia noticia;
}
