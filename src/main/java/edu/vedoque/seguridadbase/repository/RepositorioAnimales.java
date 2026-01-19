package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Animal;
import edu.vedoque.seguridadbase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioAnimales extends JpaRepository<Animal, Long> {

    // Filtrar por tipo (PERRO/GATO)
    List<Animal> findByTipo(String tipo);

    // NUEVO: Encontrar los animales que ha subido un usuario concreto
    List<Animal> findByUsuario(User usuario);
}