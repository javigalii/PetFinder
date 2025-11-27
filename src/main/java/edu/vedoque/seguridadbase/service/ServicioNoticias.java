package edu.vedoque.seguridadbase.service;

import edu.vedoque.seguridadbase.dto.NoticiaDto;
import edu.vedoque.seguridadbase.entity.MeGusta;
import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioCometario;
import edu.vedoque.seguridadbase.repository.RepositorioMeGusta;
import edu.vedoque.seguridadbase.repository.RepositorioNoticias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ServicioNoticias {
    @Autowired
    private RepositorioNoticias repositorioNoticias;
    @Autowired
    private RepositorioCometario repositorioCometario;
    @Autowired
    private RepositorioMeGusta repositorioMeGusta;


    public NoticiaDto toDto(Noticia noticia, User usuario){
        NoticiaDto noticiaDto = new NoticiaDto();
        noticiaDto.setId(noticia.getId());
        noticiaDto.setTitulo(noticia.getTitulo());
        noticiaDto.setAutor(noticia.getAutor());
        noticiaDto.setFecha(noticia.getFecha());
        noticiaDto.setImagen(noticia.getImagen());
        noticiaDto.setContenido(noticia.getContenido());

        noticiaDto.setCantidadMegusta(repositorioMeGusta.countMeGustaByNoticia(noticia));
        // Consigo la lista de MeGusta que le ha dado el usuario a esta noticia
        ArrayList<MeGusta> auxLista = repositorioMeGusta.findByNoticiaAndUsuario(noticia, usuario);
        // Si la lista contiene algo, pongo que la noticia si le gusta al usuario
        if(auxLista.size() > 0){
            noticiaDto.setLeGustaAlUsuarioActivo("❤\uFE0F");
        }else{
            // Si la lista esta vacia, al usuario no le gusta la noticia
            noticiaDto.setLeGustaAlUsuarioActivo("♡");
        }

        return noticiaDto;

    };
}
