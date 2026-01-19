package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioAnimales;
import edu.vedoque.seguridadbase.repository.UserRepository;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Para guardar el usuario editado

    @Autowired
    private RepositorioAnimales repoAnimales;

    // 1. VER MI PERFIL
    @GetMapping({"", "/"})
    public String verPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        // Obtenemos el usuario conectado
        User usuario = userService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);

        // Obtenemos los animales que ha subido este usuario
        List<Animal> misAnimales = repoAnimales.findByUsuario(usuario);
        model.addAttribute("misAnimales", misAnimales);

        return "perfil/verPerfil";
    }

    // 2. IR AL FORMULARIO DE EDICIÓN
    @GetMapping("/editar")
    public String editarPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);

        return "perfil/editarPerfil";
    }

    // 3. GUARDAR LOS CAMBIOS
    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute User usuarioForm, Authentication auth) {
        if(auth == null) return "redirect:/login";

        // Recuperamos el usuario de la BD (el "real")
        User usuarioReal = userService.findByEmail(auth.getName());

        // Actualizamos SOLO los datos de perfil (no tocamos ID, ni password, ni email)
        usuarioReal.setNombrePila(usuarioForm.getNombrePila());
        usuarioReal.setLocalizacion(usuarioForm.getLocalizacion());
        usuarioReal.setDescripcion(usuarioForm.getDescripcion());
        usuarioReal.setFotoUrl(usuarioForm.getFotoUrl());

        // Guardamos en base de datos
        userRepository.save(usuarioReal);

        return "redirect:/perfil/";
    }

    // --- NUEVO: MOSTRAR FORMULARIO PARA SUBIR ANIMAL ---
    @GetMapping("/anadir-animal")
    public String formularioAnadir(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        // Pasamos un animal vacío para que el formulario lo rellene
        model.addAttribute("animal", new Animal());

        return "perfil/anadirAnimal";
    }

    // --- NUEVO: GUARDAR EL ANIMAL EN LA BASE DE DATOS ---
    @PostMapping("/guardar-animal")
    public String guardarNuevoAnimal(@ModelAttribute Animal animal, Authentication auth) {
        if(auth == null) return "redirect:/login";

        // 1. Obtenemos el usuario conectado (el dueño)
        User usuario = userService.findByEmail(auth.getName());

        // 2. Asignamos este usuario al animal
        animal.setUsuario(usuario);

        // 3. Guardamos en la base de datos
        repoAnimales.save(animal);

        // 4. Volvemos al perfil para ver que se ha añadido
        return "redirect:/perfil/";
    }
}