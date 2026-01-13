package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioAnimales extends JpaRepository<Animal, Long> {

    // Consulta customizada para filtrar manejando nulos
    @Query("SELECT a FROM Animal a WHERE " +
            "(:tipo IS NULL OR a.tipo = :tipo) AND " +
            "(:raza IS NULL OR a.raza LIKE %:raza%) AND " +
            "(:ciudad IS NULL OR a.ciudad LIKE %:ciudad%) AND " +
            "(:sexo IS NULL OR a.sexo = :sexo) AND " +
            "(:edad IS NULL OR a.edad <= :edad) AND " +
            "(:castrado IS NULL OR a.castrado = :castrado)")
    List<Animal> buscarConFiltros(
            @Param("tipo") String tipo,
            @Param("raza") String raza,
            @Param("ciudad") String ciudad,
            @Param("sexo") String sexo,
            @Param("edad") Integer edad,
            @Param("castrado") Boolean castrado
    );
}