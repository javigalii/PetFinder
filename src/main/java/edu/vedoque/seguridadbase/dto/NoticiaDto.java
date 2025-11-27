package edu.vedoque.seguridadbase.dto;

import edu.vedoque.seguridadbase.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
public class NoticiaDto {
    private long id;
    private String titulo;
    private String contenido;
    private String imagen;
    private Date fecha;
    private User autor;

    private int cantidadMegusta;
    private String leGustaAlUsuarioActivo = "♡";
}
