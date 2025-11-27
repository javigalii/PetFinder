package edu.vedoque.seguridadbase.controller;


import edu.vedoque.seguridadbase.entity.Noticia;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioNoticias;
import edu.vedoque.seguridadbase.service.FileProcessingService;
import edu.vedoque.seguridadbase.service.UserService;
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
    public String recibeDatosFormulario(@ModelAttribute Noticia noticia, @RequestParam("fichero") MultipartFile fichero) {
        String redireccion = noticia.getId() == 0? "redirect:/crud/noticias/insertar":"redirect:/crud/noticias";
        repo.save(noticia);

        // Si el nombre del fichero no está vacío en el formulario...
        if(!fichero.isEmpty()) {
            // Recupero el nombre original del archivo
            String nombreOriginal = fichero.getOriginalFilename();
            // Me quedo con la extensión, que será la cadena de texto que está después del punto en el nombre de fichero
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")+1);
            // A la imagen que subo le pongo el nombre n-idImagen.extension
            String img = "n-" + noticia.getId() + "." + extension;
            // Uso el servicio para subir la imagen al servidor y recibo el resultado
            String resultadoSubida = fileProcessingService.uploadFile(fichero, img);
            // A la noticia le pongo como imagen el nombre que acabo de generar (n-id.extension)
            noticia.setImagen(img);
            // Vuelvo a guardar la noticia en la base de datos con este nuevo valor de imagen
            repo.save(noticia);
        }
        return redireccion;
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
    public String eliminarNoticia(@PathVariable long id) {
        // Primero comprobamos si existe la noticia
        Optional<Noticia> noticia = repo.findById(id);
        if (noticia.isPresent()) {
            // Si existe, la eliminamos
            repo.delete(noticia.get());
        }
        // Redirigimos a la lista de noticias
        return "redirect:/crud/noticias";
    }
}
