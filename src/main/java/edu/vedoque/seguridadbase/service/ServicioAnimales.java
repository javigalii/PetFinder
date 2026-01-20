package edu.vedoque.seguridadbase.service;

import edu.vedoque.seguridadbase.dto.AnimalDto;
import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.repository.RepositorioMeGustaAnimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioAnimales {

    @Autowired
    private RepositorioMeGustaAnimal repoMeGusta;

    public AnimalDto toDto(Animal animal, User usuario) {
        AnimalDto dto = new AnimalDto();
        dto.setId(animal.getId());
        dto.setNombre(animal.getNombre());
        dto.setTipo(animal.getTipo());
        dto.setRaza(animal.getRaza());
        dto.setImagenUrl(animal.getImagenUrl());

        dto.setEdad(animal.getEdad());
        dto.setLocalizacion(animal.getLocalizacion());
        dto.setSexo(animal.getSexo());
        dto.setCastrado(animal.isCastrado());

        // --- NUEVO: PASAMOS EL DUEÑO AL DTO ---
        dto.setUsuario(animal.getUsuario());

        // Lógica del Like
        if (usuario != null) {
            List<MeGustaAnimal> likes = repoMeGusta.findByAnimalAndUsuario(animal, usuario);
            dto.setLikedByCurrentUser(!likes.isEmpty());
        } else {
            dto.setLikedByCurrentUser(false);
        }
        return dto;
    }
}