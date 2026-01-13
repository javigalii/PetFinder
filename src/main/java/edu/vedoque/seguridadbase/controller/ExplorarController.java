package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.dto.AnimalDto;
import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioAnimales;
import edu.vedoque.seguridadbase.repository.RepositorioMeGustaAnimal;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ExplorarController {

    @Autowired
    private RepositorioAnimales repositorioAnimales;
    @Autowired
    private RepositorioMeGustaAnimal repositorioMeGustaAnimal;
    @Autowired
    private UserService userService;

    @GetMapping("/explorar")
    public String explorar(
            Model model,
            Authentication authentication,
            // Parámetros que vienen del formulario HTML
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) Boolean castrado
    ) {
        // 1. Obtener usuario actual
        User usuario = null;
        if(authentication != null) {
            usuario = userService.findByEmail(authentication.getName());
        }

        // 2. Limpieza básica de Strings vacíos
        if (tipo != null && tipo.isEmpty()) tipo = null;
        if (raza != null && raza.isEmpty()) raza = null;
        if (ciudad != null && ciudad.isEmpty()) ciudad = null;
        if (sexo != null && sexo.isEmpty()) sexo = null;

        // 3. Buscar en BBDD usando el repositorio
        List<Animal> animalesEntity = repositorioAnimales.buscarConFiltros(tipo, raza, ciudad, sexo, edad, castrado);

        // 4. Convertir a DTO (Añadir la info del "Me Gusta")
        ArrayList<AnimalDto> listaDto = new ArrayList<>();

        for (Animal a : animalesEntity) {
            boolean isLiked = false;
            if (usuario != null) {
                isLiked = repositorioMeGustaAnimal.existsByAnimalAndUsuario(a, usuario);
            }
            // Creamos el DTO con el animal y el booleano
            listaDto.add(new AnimalDto(a, isLiked));
        }

        model.addAttribute("animales", listaDto);
        return "explorar";
    }

    @PostMapping("/megusta/toggle")
    public String toggleMeGusta(@RequestParam long idAnimal, Authentication authentication) {
        if (authentication == null) return "redirect:/login";

        User usuario = userService.findByEmail(authentication.getName());
        Animal animal = repositorioAnimales.findById(idAnimal).orElse(null);

        if(animal != null) {
            ArrayList<MeGustaAnimal> likes = repositorioMeGustaAnimal.findByAnimalAndUsuario(animal, usuario);

            if (!likes.isEmpty()) {
                // Si ya existe, lo borramos (quitar like)
                repositorioMeGustaAnimal.delete(likes.get(0));
            } else {
                // Si no existe, lo creamos (dar like)
                MeGustaAnimal nuevoLike = new MeGustaAnimal();
                nuevoLike.setAnimal(animal);
                nuevoLike.setUsuario(usuario);
                repositorioMeGustaAnimal.save(nuevoLike);
            }
        }
        return "redirect:/explorar";
    }
}