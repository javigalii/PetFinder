package edu.vedoque.seguridadbase.repository;


import edu.vedoque.seguridadbase.entity.Comentario;
import edu.vedoque.seguridadbase.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RepositorioCometario extends JpaRepository<Comentario,Long> {
    // Devuelve la lista de comentarios que pertenecen a una noticia que se pasa como argumento.
    public ArrayList<Comentario> findByNoticia(Noticia noticia);
}
