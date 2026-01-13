package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface RepositorioMeGustaAnimal extends JpaRepository<MeGustaAnimal, Long> {
    // Para ver si existe un like concreto (para borrarlo o crearlo)
    ArrayList<MeGustaAnimal> findByAnimalAndUsuario(Animal animal, User usuario);

    // Para saber si pintar el corazón rojo (devuelve true/false)
    boolean existsByAnimalAndUsuario(Animal animal, User usuario);
}