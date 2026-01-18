package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.MeGustaAnimal;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioMeGustaAnimal extends JpaRepository<MeGustaAnimal, Long> {
    // Para saber si le gusta este animal
    List<MeGustaAnimal> findByAnimalAndUsuario(Animal animal, User usuario);

    // Para la lista de "Mis Favoritos"
    List<MeGustaAnimal> findByUsuario(User usuario);
}