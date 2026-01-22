package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.dto.AnimalDto;
import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioAnimales;
import edu.vedoque.seguridadbase.repository.RepositorioMeGustaAnimal;
import edu.vedoque.seguridadbase.service.ServicioAnimales;
import edu.vedoque.seguridadbase.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AnimalController {

    @Autowired private RepositorioAnimales repoAnimales;
    @Autowired private RepositorioMeGustaAnimal repoMeGusta;
    @Autowired private UserService userService;
    @Autowired private ServicioAnimales servicioAnimales;

    // 1. LISTA GENERAL CON TODOS LOS FILTROS NUEVOS
    @GetMapping("/")
    public String lista(Model model,
                        Authentication auth,
                        @RequestParam(required=false) String tipo,
                        @RequestParam(required=false) String ciudad,
                        @RequestParam(required=false) String raza,
                        @RequestParam(required=false) String sexo,
                        @RequestParam(required=false) Integer edad,
                        @RequestParam(required=false) Boolean castrado,
                        @RequestParam(required=false) Boolean favoritos) {

        User usuario = (auth != null) ? userService.findByEmail(auth.getName()) : null;

        List<Animal> animalesBrutos;

        // 1. LÓGICA DE FAVORITOS VS TODOS
        if (Boolean.TRUE.equals(favoritos) && usuario != null) {
            // Si el switch "Mis Favoritos" está activo, traemos solo los likes
            List<MeGustaAnimal> likes = repoMeGusta.findByUsuario(usuario);
            animalesBrutos = likes.stream().map(MeGustaAnimal::getAnimal).collect(Collectors.toList());
        } else {
            // Si no, traemos todos (o filtramos por tipo si existe para optimizar un poco)
            if (tipo != null && !tipo.isEmpty()) {
                animalesBrutos = repoAnimales.findByTipo(tipo);
            } else {
                animalesBrutos = repoAnimales.findAll();
            }
        }

        // 2. APLICAR RESTO DE FILTROS (Ciudad, Raza, Sexo, Edad, Castrado)
        // Usamos Java Streams para filtrar la lista cargada
        List<Animal> animalesFiltrados = animalesBrutos.stream()
                .filter(a -> tipo == null || tipo.isEmpty() || a.getTipo().equalsIgnoreCase(tipo))
                .filter(a -> ciudad == null || ciudad.isEmpty() || (a.getLocalizacion() != null && a.getLocalizacion().toLowerCase().contains(ciudad.toLowerCase())))
                .filter(a -> raza == null || raza.isEmpty() || (a.getRaza() != null && a.getRaza().equalsIgnoreCase(raza)))
                .filter(a -> sexo == null || sexo.isEmpty() || (a.getSexo() != null && a.getSexo().equalsIgnoreCase(sexo)))
                .filter(a -> edad == null || a.getEdad() <= edad) // Filtra animales con esa edad o menos
                .filter(a -> castrado == null || a.isCastrado() == castrado)
                .collect(Collectors.toList());

        // 3. CONVERTIR A DTO (Para saber si el usuario le dio like a cada uno)
        List<AnimalDto> listaDto = new ArrayList<>();
        for (Animal a : animalesFiltrados) {
            listaDto.add(servicioAnimales.toDto(a, usuario));
        }

        model.addAttribute("animales", listaDto);
        return "index";
    }

    // 2. DETALLE DEL ANIMAL
    @GetMapping("/detalle/{id}")
    public String detalle(Model model, @PathVariable Long id, Authentication auth) {
        User usuario = (auth != null) ? userService.findByEmail(auth.getName()) : null;

        Animal animal = repoAnimales.findById(id).orElse(null);
        if(animal == null) {
            return "redirect:/";
        }

        AnimalDto animalDto = servicioAnimales.toDto(animal, usuario);
        model.addAttribute("animal", animalDto);
        return "/detalle";
    }

    // 3. DAR/QUITAR ME GUSTA
    @GetMapping("/megusta/{id}")
    public String meGusta(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Optional<Animal> animalOpt = repoAnimales.findById(id);

        if(animalOpt.isPresent()) {
            Animal animal = animalOpt.get();
            List<MeGustaAnimal> likes = repoMeGusta.findByAnimalAndUsuario(animal, usuario);

            if(!likes.isEmpty()) {
                repoMeGusta.delete(likes.get(0)); // Quitar like
            } else {
                MeGustaAnimal nuevoLike = new MeGustaAnimal(); // Dar like
                nuevoLike.setAnimal(animal);
                nuevoLike.setUsuario(usuario);
                repoMeGusta.save(nuevoLike);
            }
        }

        // MEJORA: Redirige a la página desde donde viniste (lista o detalle)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}