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
}