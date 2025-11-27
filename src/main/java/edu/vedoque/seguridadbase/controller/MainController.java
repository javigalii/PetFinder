package edu.vedoque.seguridadbase.controller;


import edu.vedoque.seguridadbase.dto.NoticiaDto;
import edu.vedoque.seguridadbase.entity.Comentario;
import edu.vedoque.seguridadbase.entity.MeGusta;
import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioCometario;
import edu.vedoque.seguridadbase.repository.RepositorioMeGusta;
import edu.vedoque.seguridadbase.repository.RepositorioNoticias;
import edu.vedoque.seguridadbase.service.ServicioNoticias;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class MainController {
    @Autowired
    private RepositorioNoticias repositorioNoticias;
    @Autowired
    private RepositorioCometario repositorioCometario;
    @Autowired
    private UserService userService;
    @Autowired
    private RepositorioMeGusta repositorioMeGusta;
    @Autowired
    ServicioNoticias servicioNoticias;

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        User usuario=null;
        if(authentication != null) {
            usuario = userService.findByEmail(authentication.getName());
        }

        ArrayList<NoticiaDto> listaDto = new ArrayList<>();
        ArrayList<Noticia> listaNoticias = (ArrayList<Noticia>) repositorioNoticias.findAll();
        for  (Noticia noticia : listaNoticias) {
            if(usuario!=null) {
                listaDto.add(servicioNoticias.toDto(noticia, usuario));
            }else{
                listaDto.add(servicioNoticias.toDto(noticia, usuario));
            }

        }
        model.addAttribute( "noticias", listaDto);
        return "index";
    }

    @GetMapping("/noticia/{id}")
    public String noticia(Model model, @PathVariable long id) {
        // Recupero la noticia que tiene como id el que me pasan en la url como pathvariable
        Noticia noticia = repositorioNoticias.findById(id).get();
        // Añado ese noticia al modelo para que esté disponible en la vista
        model.addAttribute("noticia", repositorioNoticias.findById(id).get());
        // Usando la noticia recupero todos los comentarios relacionados y los pongo en el modelo para que estén disponibles en la vista
        model.addAttribute("listaComentarios", repositorioCometario.findByNoticia(noticia));
        // Creo un comentario vacio
        Comentario auxComentario = new Comentario();
        // A ese comentario lo relaciono con la noticia anterior
        auxComentario.setNoticia(noticia);
        // Envío ese comentario a la vista a traves del modelo para que el usuario lo complete
        // Llamo al comentario auxComentario y es el nombre que debo usar en la vista
        model.addAttribute("auxComentario", auxComentario);
        return "verNoticia";
    }

    @PostMapping("/comentario/insertar")
    public String insertar(@ModelAttribute Comentario comentario, Model model) {
        comentario.setFecha(Date.valueOf(LocalDate.now()));
        repositorioCometario.save(comentario);
        return "redirect:/noticia/"+comentario.getNoticia().getId();
    }

    @GetMapping("/megusta/{idNoticia}")
    public String megusta(Model model, @PathVariable long idNoticia, Authentication authentication) {
        User usuario = userService.findByEmail(authentication.getName());
        Noticia noticia = repositorioNoticias.findById(idNoticia).get();

        ArrayList<MeGusta> auxLista = repositorioMeGusta.findByNoticiaAndUsuario(noticia, usuario);

        if(auxLista.size()>0){
            repositorioMeGusta.delete(auxLista.get(0));
        }else {
            MeGusta meGusta = new MeGusta();
            meGusta.setNoticia(noticia);
            meGusta.setUsuario(usuario);
            repositorioMeGusta.save(meGusta);
        }
        return "redirect:/";
    }
}
