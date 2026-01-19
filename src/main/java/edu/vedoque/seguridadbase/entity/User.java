package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pf_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    // --- NUEVOS CAMPOS DE PERFIL ---
    private String nombrePila; // Nombre real del usuario (ej: "Juan Pérez")
    private String fotoUrl;    // URL de su foto de perfil
    private String descripcion; // Bio o descripción
    private String localizacion; // Ciudad

    // Un usuario puede subir muchos animales
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Animal> animalesSubidos;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new java.util.ArrayList<>();
}