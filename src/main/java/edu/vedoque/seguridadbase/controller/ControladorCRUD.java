package edu.vedoque.seguridadbase.controller;


import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioCometario;
import edu.vedoque.seguridadbase.repository.RepositorioMeGusta;
import edu.vedoque.seguridadbase.repository.RepositorioNoticias;
import edu.vedoque.seguridadbase.service.FileProcessingService;
import edu.vedoque.seguridadbase.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class ControladorCRUD {
    @Autowired
    RepositorioNoticias repo;
    @Autowired
    private FileProcessingService fileProcessingService;
    @Autowired
    private UserService userService;
    @Autowired
    private RepositorioMeGusta repoLikes;
    @Autowired
    private RepositorioCometario repoComentarios;

    @GetMapping("/crud/noticias")
    public String curd(Model model, Authentication authentication) {
        User autor = userService.findByEmail(authentication.getName());
        model.addAttribute("noticias", repo.findByAutor(autor));
        return "listaNoticias";
    }

    @GetMapping("/crud/noticias/insertar")
    public String muestraFormulario(Model model, Authentication authentication) {
        // Creamos una noticia en blanco
        Noticia noticia = new Noticia();
        // A la noticia en blanco le ponemos la fecha actual
        noticia.setFecha(Date.valueOf(LocalDate.now()));
        // Consigo el usuario activo a través de Authentication
        User autor=userService.findByEmail(authentication.getName());
        // Una vez que tengo el autor, lo pongo en la noticia
        noticia.setAutor(autor);
        model.addAttribute("noticia", noticia);
        return "formularioNoticias";
    }

    @PostMapping("/crud/noticias/insertar")
    public String recibeDatosFormulario(@ModelAttribute Noticia noticia,
                                        @RequestParam("fichero") MultipartFile fichero,
                                        Authentication authentication) { // 1. Añadimos Authentication

        // 2. Recuperamos al usuario actual
        User autor = userService.findByEmail(authentication.getName());

        // 3. SE LO ASIGNAMOS EXPLÍCITAMENTE A LA NOTICIA ANTES DE GUARDAR
        noticia.setAutor(autor);

        // 4. (Opcional) Si es una inserción nueva, aseguramos la fecha actual
        if (noticia.getId() == 0) {
            noticia.setFecha(Date.valueOf(LocalDate.now()));
        }

        repo.save(noticia);

        // Lógica de la imagen...
        if(!fichero.isEmpty()) {
            String nombreOriginal = fichero.getOriginalFilename();
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")+1);
            String img = "n-" + noticia.getId() + "." + extension;
            fileProcessingService.uploadFile(fichero, img);
            noticia.setImagen(img);
            repo.save(noticia);
        }

        // Redirigimos siempre a la lista para ver que se ha creado bien
        return "redirect:/crud/noticias";
    }

    @GetMapping("/crud/noticias/modificar/{id}")
    public String modificarNoticia( @PathVariable long id,Model model) {
        Optional<Noticia> noticia = repo.findById(id);
        if (noticia.isPresent()) {
            model.addAttribute("noticia", noticia.get());
            return "formularioNoticias";
        }
        return "redirect:/crud/noticias";
    }

    @GetMapping("/crud/noticias/eliminar/{id}")
    @Transactional
    public String eliminarNoticia(@PathVariable long id) {
        Optional<Noticia> noticia = repo.findById(id);

        if (noticia.isPresent()) {
            Noticia n = noticia.get();

            // A. Borramos los likes (esto ya lo tenías)
            repoLikes.deleteByNoticia(n);

            // B. Borramos los comentarios (ESTO ES LO QUE FALTABA)
            // El log indicaba que la tabla 'pf_comentarios' bloqueaba el borrado
            repoComentarios.deleteByNoticia(n);

            // C. Ahora sí podemos borrar la noticia padre
            repo.delete(n);
        }
        return "redirect:/crud/noticias";
    }

    @PostMapping("/crud/noticias/modificar")
    public String procesarModificacion(@ModelAttribute Noticia noticia,
                                       @RequestParam("fichero") MultipartFile fichero) {

        // PASO 1: Buscamos la noticia original para no perder el Autor ni la Fecha
        Optional<Noticia> noticiaExistente = repo.findById(noticia.getId());

        if (noticiaExistente.isPresent()) {
            Noticia n = noticiaExistente.get();

            // PASO 2: Actualizamos solo los campos editables
            n.setTitulo(noticia.getTitulo());
            n.setContenido(noticia.getContenido());
            // NOTA: No tocamos n.setAutor() ni n.setFecha(), así se mantienen los originales.

            // PASO 3: Gestión del fichero (solo si el usuario subió uno nuevo)
            if (!fichero.isEmpty()) {
                // Generamos nombre: n-ID.extension
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                String nombreImagen = "n-" + n.getId() + "." + extension;

                // Subimos el archivo usando tu servicio
                fileProcessingService.uploadFile(fichero, nombreImagen);

                // Actualizamos la ruta en la noticia
                n.setImagen(nombreImagen);
            }

            // PASO 4: Guardamos los cambios
            repo.save(n);
        }

        return "redirect:/crud/noticias";
    }
}
