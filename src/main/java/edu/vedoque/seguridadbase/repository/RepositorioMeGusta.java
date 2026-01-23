package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.MeGusta;
import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import jakarta.transaction.Transactional; // Asegúrate de importar esto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioMeGusta extends JpaRepository<MeGusta, Long> {

    int countMeGustaByNoticia(Noticia noticia);

    List<MeGusta> findByNoticiaAndUsuario(Noticia noticia, User usuario);

    long countByNoticia(Noticia noticia);

    @Transactional // Necesario para permitir la operación de DELETE
    void deleteByNoticia(Noticia noticia);
}