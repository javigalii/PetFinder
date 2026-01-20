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
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositorioAnimales repoAnimales;

    // INYECTAMOS EL SERVICIO DE FICHEROS
    @Autowired
    private FileProcessingService fileProcessingService;

    // 1. VER MI PERFIL
    @GetMapping({"", "/"})
    public String verPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);

        List<Animal> misAnimales = repoAnimales.findByUsuario(usuario);
        model.addAttribute("misAnimales", misAnimales);

        return "perfil/verPerfil";
    }

    // 2. IR AL FORMULARIO DE EDICIÓN DE USUARIO
    @GetMapping("/editar")
    public String editarPerfil(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        model.addAttribute("usuario", usuario);

        return "perfil/editarPerfil";
    }

    // 3. GUARDAR LOS CAMBIOS DEL USUARIO (CON FOTO)
    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute User usuarioForm,
                                @RequestParam("fichero") MultipartFile fichero, // <--- Nuevo parámetro
                                Authentication auth) {
        if(auth == null) return "redirect:/login";

        // Recuperamos el usuario real de la base de datos
        User usuarioReal = userService.findByEmail(auth.getName());

        // Actualizamos datos de texto
        usuarioReal.setNombrePila(usuarioForm.getNombrePila());
        usuarioReal.setLocalizacion(usuarioForm.getLocalizacion());
        usuarioReal.setDescripcion(usuarioForm.getDescripcion());

        // GESTIÓN DE LA FOTO DE PERFIL
        if (!fichero.isEmpty()) {
            try {
                // 1. Sacamos la extensión (jpg, png...)
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = "jpg"; // por defecto
                if (nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                }

                // 2. Creamos un nombre único: perfil-IDUSUARIO.extensión
                String nombreImagen = "perfil-" + usuarioReal.getId() + "." + extension;

                // 3. Subimos el archivo
                fileProcessingService.uploadFile(fichero, nombreImagen);

                // 4. Guardamos el nombre en la base de datos
                usuarioReal.setFotoUrl(nombreImagen);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Si fichero.isEmpty(), no hacemos nada y mantenemos la foto anterior

        userRepository.save(usuarioReal);

        return "redirect:/perfil/";
    }

    // 4. MOSTRAR FORMULARIO PARA SUBIR ANIMAL
    @GetMapping("/anadir-animal")
    public String formularioAnadir(Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";
        model.addAttribute("animal", new Animal());
        return "perfil/anadirAnimal";
    }

    // 5. GUARDAR NUEVO ANIMAL (CON SUBIDA DE IMAGEN)
    @PostMapping("/guardar-animal")
    public String guardarNuevoAnimal(@ModelAttribute Animal animal,
                                     @RequestParam("fichero") MultipartFile fichero, // <--- Nuevo parámetro
                                     Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        animal.setUsuario(usuario);

        // Primero guardamos para generar el ID
        repoAnimales.save(animal);

        // Si el usuario ha subido una foto...
        if (!fichero.isEmpty()) {
            try {
                // Generamos un nombre único: animal-ID.extension
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = "";
                if (nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                } else {
                    extension = "jpg"; // Extensión por defecto si falla
                }

                String nombreImagen = "animal-" + animal.getId() + "." + extension;

                // Subimos el archivo usando tu servicio
                fileProcessingService.uploadFile(fichero, nombreImagen);

                // Guardamos el nombre del fichero en el campo imagenUrl
                animal.setImagenUrl(nombreImagen);

                // Actualizamos el animal en la BD
                repoAnimales.save(animal);

            } catch (Exception e) {
                e.printStackTrace(); // O manejar error
            }
        } else {
            // Si no sube foto, ponemos una por defecto
            animal.setImagenUrl("default-pet.png");
            repoAnimales.save(animal);
        }

        return "redirect:/perfil/";
    }

    // 6. BORRAR ANIMAL
    @GetMapping("/eliminar-animal/{id}")
    public String eliminarAnimal(@PathVariable Long id, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animal = repoAnimales.findById(id).orElse(null);

        if (animal != null && animal.getUsuario().getId().equals(usuario.getId())) {
            repoAnimales.delete(animal);
        }
        return "redirect:/perfil/";
    }

    // 7. MOSTRAR FORMULARIO DE EDICIÓN DE ANIMAL
    @GetMapping("/editar-animal/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animal = repoAnimales.findById(id).orElse(null);

        if (animal != null && animal.getUsuario().getId().equals(usuario.getId())) {
            model.addAttribute("animal", animal);
            return "perfil/editarAnimal";
        }
        return "redirect:/perfil/";
    }

    // 8. ACTUALIZAR ANIMAL (CON SUBIDA DE IMAGEN)
    @PostMapping("/actualizar-animal")
    public String actualizarAnimal(@ModelAttribute Animal animalForm,
                                   @RequestParam("fichero") MultipartFile fichero, // <--- Nuevo parámetro
                                   Authentication auth) {
        if(auth == null) return "redirect:/login";

        User usuario = userService.findByEmail(auth.getName());
        Animal animalExistente = repoAnimales.findById(animalForm.getId()).orElse(null);

        if(animalExistente != null && animalExistente.getUsuario().getId().equals(usuario.getId())) {
            // Actualizamos datos básicos
            animalExistente.setNombre(animalForm.getNombre());
            animalExistente.setTipo(animalForm.getTipo());
            animalExistente.setRaza(animalForm.getRaza());
            animalExistente.setEdad(animalForm.getEdad());
            animalExistente.setSexo(animalForm.getSexo());
            animalExistente.setLocalizacion(animalForm.getLocalizacion());
            animalExistente.setCastrado(animalForm.isCastrado());

            // GESTIÓN DE LA IMAGEN EN EDICIÓN
            if (!fichero.isEmpty()) {
                // Si sube nueva foto, la procesamos y sobrescribimos la anterior
                String nombreOriginal = fichero.getOriginalFilename();
                String extension = "jpg";
                if(nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1);
                }

                String nombreImagen = "animal-" + animalExistente.getId() + "." + extension;

                fileProcessingService.uploadFile(fichero, nombreImagen);
                animalExistente.setImagenUrl(nombreImagen);
            }
            // Si fichero.isEmpty(), NO tocamos animalExistente.imagenUrl (se queda la vieja)

            repoAnimales.save(animalExistente);
        }

        return "redirect:/perfil/";
    }

    // 9. VER PERFIL PÚBLICO
    @GetMapping("/usuario/{id}")
    public String verPerfilPublico(@PathVariable Long id, Model model) {
        User usuario = userRepository.findById(id).orElse(null);
        if (usuario == null) return "redirect:/animales/lista";

        List<Animal> animalesUsuario = repoAnimales.findByUsuario(usuario);
        model.addAttribute("usuarioPublico", usuario);
        model.addAttribute("animalesPublicos", animalesUsuario);

        return "perfil/verPerfilPublico";
    }
}