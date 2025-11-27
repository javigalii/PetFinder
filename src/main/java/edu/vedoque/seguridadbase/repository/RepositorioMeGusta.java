package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.MeGusta;
import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RepositorioMeGusta extends JpaRepository<MeGusta, Long> {
    int countMeGustaByNoticia(Noticia noticia);
    ArrayList<MeGusta> findByNoticiaAndUsuario(Noticia noticia, User usuario);
}
