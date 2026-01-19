package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.dto.AnimalDto;
import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioAnimales;
import edu.vedoque.seguridadbase.repository.RepositorioMeGustaAnimal;
import edu.vedoque.seguridadbase.service.ServicioAnimales;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/animales")
public class AnimalController {

    @Autowired private RepositorioAnimales repoAnimales;
    @Autowired private RepositorioMeGustaAnimal repoMeGusta;
    @Autowired private UserService userService;
    @Autowired private ServicioAnimales servicioAnimales;

    // 1. LISTA GENERAL (Catálogo)
    @GetMapping("/lista")
    public String lista(Model model, Authentication auth, @RequestParam(required=false) String tipo) {
        User usuario = (auth != null) ? userService.findByEmail(auth.getName()) : null;

        List<Animal> animales;
        if(tipo != null && !tipo.isEmpty()) {
            animales = repoAnimales.findByTipo(tipo); // Filtrar perro/gato
        } else {
            animales = repoAnimales.findAll(); // Todos
        }

        List<AnimalDto> listaDto = new ArrayList<>();
        for (Animal a : animales) {
            listaDto.add(servicioAnimales.toDto(a, usuario));
        }

        model.addAttribute("animales", listaDto);
        return "animales/lista";
    }

    // 2. DETALLE DEL ANIMAL
    @GetMapping("/detalle/{id}")
    public String detalle(Model model, @PathVariable Long id, Authentication auth) {
        User usuario = (auth != null) ? userService.findByEmail(auth.getName()) : null;

        Animal animal = repoAnimales.findById(id).orElse(null);
        if(animal == null) {
            return "redirect:/animales/lista";
        }

        AnimalDto animalDto = servicioAnimales.toDto(animal, usuario);
        model.addAttribute("animal", animalDto);
        return "animales/detalle";
    }

    // 3. DAR/QUITAR ME GUSTA
    @GetMapping("/megusta/{id}")
    public String meGusta(@PathVariable Long id, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Optional<Animal> animalOpt = repoAnimales.findById(id);

        if(animalOpt.isPresent()) {
            Animal animal = animalOpt.get();
            List<MeGustaAnimal> likes = repoMeGusta.findByAnimalAndUsuario(animal, usuario);

            if(!likes.isEmpty()) {
                // Si ya existe, lo borramos (QUITAR LIKE)
                repoMeGusta.delete(likes.get(0));
            } else {
                // Si no existe, lo creamos (DAR LIKE)
                MeGustaAnimal nuevoLike = new MeGustaAnimal();
                nuevoLike.setAnimal(animal);
                nuevoLike.setUsuario(usuario);
                repoMeGusta.save(nuevoLike);
            }
        }

        // Truco: Redirigimos a la página anterior para que no te saque de donde estás
        // Si no funciona en tu navegador, cámbialo por return "redirect:/animales/lista";
        return "redirect:/animales/lista";
    }

    // 4. MIS FAVORITOS (VISTA APARTE)
    @GetMapping("/favoritos")
    public String misFavoritos(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());

        // Buscamos solo los likes de ESTE usuario
        List<MeGustaAnimal> misLikes = repoMeGusta.findByUsuario(usuario);

        List<AnimalDto> listaDto = new ArrayList<>();
        for (MeGustaAnimal like : misLikes) {
            // Convertimos el animal asociado al like
            listaDto.add(servicioAnimales.toDto(like.getAnimal(), usuario));
        }

        model.addAttribute("animales", listaDto);
        return "animales/favoritos"; // Carga la vista favoritos.html
    }


}