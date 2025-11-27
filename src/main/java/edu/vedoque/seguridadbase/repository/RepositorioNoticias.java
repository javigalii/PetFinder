package edu.vedoque.seguridadbase.repository;


import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface RepositorioNoticias extends JpaRepository<Noticia,Long> {
    public ArrayList<Noticia> findByAutor(User autor);
}
