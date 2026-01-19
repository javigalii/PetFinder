package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioAnimales extends JpaRepository<Animal, Long> {
    List<Animal> findByTipo(String tipo);

    // Nuevo: Buscar animales por dueño
    List<Animal> findByUsuario(User usuario);
}