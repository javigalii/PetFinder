package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.entity.Comentario;
import edu.vedoque.seguridadbase.entity.MeGusta;
import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioCometario;
import edu.vedoque.seguridadbase.repository.RepositorioMeGusta;
import edu.vedoque.seguridadbase.repository.RepositorioNoticias;
import edu.vedoque.seguridadbase.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;

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

    // He quitado ServicioNoticias porque ahora usamos la Entidad directamente y no hacía falta.

    // 1. LISTADO DE NOTICIAS (BLOG)
    @GetMapping("/blog")
    public String index(Model model, Authentication authentication) {

        // Cargar noticias ordenadas por fecha descendente (lo más nuevo arriba)
        List<Noticia> listaNoticias = repositorioNoticias.findAll(Sort.by(Sort.Direction.DESC, "fecha"));

        User usuario = null;
        if(authentication != null) {
            usuario = userService.findByEmail(authentication.getName());
        }

        // Calculamos los likes para cada noticia
        for (Noticia noticia : listaNoticias) {

            // 1. Calcular el número total de likes (para que se vea el numerito)
            // Hacemos cast a (int) porque el count devuelve long
            noticia.setCantidadMegusta((int) repositorioMeGusta.countByNoticia(noticia));

            // 2. Si el usuario está conectado, vemos si él le dio like
            if(usuario != null) {
                List<MeGusta> likes = repositorioMeGusta.findByNoticiaAndUsuario(noticia, usuario);
                noticia.setLikedByCurrentUser(!likes.isEmpty());
            }
        }

        model.addAttribute("noticias", listaNoticias);
        return "blog";
    }

    // 2. VER NOTICIA EN DETALLE
    @GetMapping("/noticia/{id}")
    public String noticia(Model model, @PathVariable long id, Authentication authentication) {

        // Usamos orElse(null) para no romper la web si el ID no existe
        Noticia noticia = repositorioNoticias.findById(id).orElse(null);

        if (noticia == null) {
            return "redirect:/blog";
        }

        // --- LÓGICA DE LIKES TAMBIÉN AQUÍ ---
        // Calculamos los likes también para la vista de detalle
        noticia.setCantidadMegusta((int) repositorioMeGusta.countByNoticia(noticia));

        if (authentication != null) {
            User usuario = userService.findByEmail(authentication.getName());
            List<MeGusta> likes = repositorioMeGusta.findByNoticiaAndUsuario(noticia, usuario);
            noticia.setLikedByCurrentUser(!likes.isEmpty());
        }
        // ------------------------------------

        model.addAttribute("noticia", noticia);
        model.addAttribute("listaComentarios", repositorioCometario.findByNoticia(noticia));

        // Preparamos el formulario para nuevo comentario
        Comentario auxComentario = new Comentario();
        auxComentario.setNoticia(noticia);
        model.addAttribute("auxComentario", auxComentario);

        return "verNoticia";
    }

    // 3. INSERTAR COMENTARIO
    @PostMapping("/comentario/insertar")
    public String insertar(@ModelAttribute Comentario comentario, Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        comentario.setFecha(Date.valueOf(LocalDate.now()));

        User usuario = userService.findByEmail(authentication.getName());
        comentario.setUsuario(usuario);

        repositorioCometario.save(comentario);

        return "redirect:/noticia/" + comentario.getNoticia().getId();
    }

    // 4. DAR / QUITAR ME GUSTA (NOTICIAS)
    @GetMapping("/megusta/noticia/{idNoticia}")
    public String megustaNoticia(@PathVariable long idNoticia, Authentication authentication, HttpServletRequest request) {

        if(authentication == null) return "redirect:/login";

        User usuario = userService.findByEmail(authentication.getName());
        Noticia noticia = repositorioNoticias.findById(idNoticia).orElse(null);

        if (noticia != null && usuario != null) {
            List<MeGusta> auxLista = repositorioMeGusta.findByNoticiaAndUsuario(noticia, usuario);

            if(!auxLista.isEmpty()){
                // Si ya existe, lo borramos (quitar like)
                repositorioMeGusta.delete(auxLista.get(0));
            } else {
                // Si no existe, lo creamos (dar like)
                MeGusta meGusta = new MeGusta();
                meGusta.setNoticia(noticia);
                meGusta.setUsuario(usuario);
                repositorioMeGusta.save(meGusta);
            }
        }

        // Redirige a la página desde donde pulsaste el botón
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/blog");
    }
}