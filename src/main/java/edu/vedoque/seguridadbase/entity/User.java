package edu.vedoque.seguridadbase.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // <--- 1. IMPORTANTE: Añadir esta importación

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
    private String nombrePila;
    private String fotoUrl;
    private String descripcion;
    private String localizacion;

    // RELACIÓN: Un usuario sube muchos animales
    // 2. IMPORTANTE: @ToString.Exclude rompe el bucle infinito.
    // Le dice a Lombok: "Cuando imprimas un User, NO imprimas esta lista".
    @ToString.Exclude
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Animal> animalesSubidos;

    // NOTA: Si tienes una lista de noticias aquí (aunque no salga en el código que me pasaste),
    // también debes ponerle @ToString.Exclude:
    // @ToString.Exclude
    // @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    // private List<Noticia> noticias;

    // RELACIÓN: Roles de seguridad
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new ArrayList<>();
}