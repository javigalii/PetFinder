package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "pf_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    // --- NUEVOS CAMPOS DE PERFIL ---
    private String nombrePila;   // Ej: "Laura García"
    private String fotoUrl;      // URL de la imagen
    private String descripcion;  // "Soy amante de los gatos..."
    private String localizacion; // "Madrid"

    // RELACIÓN: Un usuario sube muchos animales
    // 'mappedBy' indica que la clave ajena está en la tabla Animal (campo 'usuario')
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Animal> animalesSubidos;

    // RELACIÓN: Roles de seguridad (ADMIN, USER)
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new ArrayList<>();
}