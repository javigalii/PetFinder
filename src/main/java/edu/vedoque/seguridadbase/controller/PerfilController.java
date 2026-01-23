package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioAnimales;
import edu.vedoque.seguridadbase.repository.UserRepository;
import edu.vedoque.seguridadbase.service.FileProcessingService;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositorioAnimales repoAnimales;

    @Autowired
    private FileProcessingService fileProcessingService;

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());

        if (usuario == null) {
            return "redirect:/logout";
        }

        model.addAttribute("usuario", usuario);

        List<Animal> misAnimales = repoAnimales.findByUsuario(usuario);
        model.addAttribute("misAnimales", misAnimales);

        return "verPerfil";
    }

    @GetMapping("/editar")
    public String editarPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);

        return "editarPerfil";
    }

    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute User usuarioForm,
                                @RequestParam("fichero") MultipartFile fichero,
                                Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuarioReal = userService.findByEmail(auth.getName());
        if(usuarioReal == null) return "redirect:/logout";

        usuarioReal.setNombrePila(usuarioForm.getNombrePila());
        usuarioReal.setLocalizacion(usuarioForm.getLocalizacion());
        usuarioReal.setDescripcion(usuarioForm.getDescripcion());

        if (!fichero.isEmpty()) {
            try {
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = "jpg";
                if (nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                }
                String nombreImagen = "perfil-" + usuarioReal.getId() + "." + extension;
                fileProcessingService.uploadFile(fichero, nombreImagen);
                usuarioReal.setFotoUrl(nombreImagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userRepository.save(usuarioReal);
        return "redirect:/perfil";
    }

    @GetMapping("/anadir-animal")
    public String formularioAnadir(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";
        model.addAttribute("animal", new Animal());
        return "anadirAnimal";
    }

    @PostMapping("/guardar-animal")
    public String guardarNuevoAnimal(@ModelAttribute Animal animal,
                                     @RequestParam("fichero") MultipartFile fichero,
                                     Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        if(usuario == null) return "redirect:/logout";

        animal.setUsuario(usuario);
        repoAnimales.save(animal);

        if (!fichero.isEmpty()) {
            try {
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = (nombreOriginal != null && nombreOriginal.contains(".")) ?
                        nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1) : "jpg";

                String nombreImagen = "animal-" + animal.getId() + "." + extension;
                fileProcessingService.uploadFile(fichero, nombreImagen);
                animal.setImagenUrl(nombreImagen);
                repoAnimales.save(animal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            animal.setImagenUrl("default-pet.png");
            repoAnimales.save(animal);
        }

        return "redirect:/perfil";
    }

    @GetMapping("/eliminar-animal/{id}")
    public String eliminarAnimal(@PathVariable Long id, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animal = repoAnimales.findById(id).orElse(null);

        if (animal != null && usuario != null && animal.getUsuario().getId().equals(usuario.getId())) {
            repoAnimales.delete(animal);
        }
        return "redirect:/perfil";
    }

    @GetMapping("/editar-animal/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animal = repoAnimales.findById(id).orElse(null);

        if (animal != null && usuario != null && animal.getUsuario().getId().equals(usuario.getId())) {
            model.addAttribute("animal", animal);
            return "editarAnimal";
        }
        return "redirect:/perfil";
    }

    @PostMapping("/actualizar-animal")
    public String actualizarAnimal(@ModelAttribute Animal animalForm,
                                   @RequestParam("fichero") MultipartFile fichero,
                                   Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animalExistente = repoAnimales.findById(animalForm.getId()).orElse(null);

        if(animalExistente != null && usuario != null && animalExistente.getUsuario().getId().equals(usuario.getId())) {
            animalExistente.setNombre(animalForm.getNombre());
            animalExistente.setTipo(animalForm.getTipo());
            animalExistente.setRaza(animalForm.getRaza());
            animalExistente.setEdad(animalForm.getEdad());
            animalExistente.setSexo(animalForm.getSexo());
            animalExistente.setLocalizacion(animalForm.getLocalizacion());
            animalExistente.setCastrado(animalForm.isCastrado());

            if (!fichero.isEmpty()) {
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = "jpg";
                if(nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                }
                String nombreImagen = "animal-" + animalExistente.getId() + "." + extension;
                fileProcessingService.uploadFile(fichero, nombreImagen);
                animalExistente.setImagenUrl(nombreImagen);
            }
            repoAnimales.save(animalExistente);
        }

        return "redirect:/perfil";
    }
    
    @GetMapping("/usuario/{id}")
    public String verPerfilPublico(@PathVariable Long id, Model model) {
        User usuario = userRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/";

        List<Animal> animalesUsuario = repoAnimales.findByUsuario(usuario);
        model.addAttribute("usuarioPublico", usuario);
        model.addAttribute("animalesPublicos", animalesUsuario);

        return "verPerfilPublico";
    }
}