package edu.vedoque.seguridadbase.dto;

import edu.vedoque.seguridadbase.entity.Animal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimalDto {
    private Animal animal; // El objeto animal completo
    private boolean liked; // true si el corazón debe salir rojo
}