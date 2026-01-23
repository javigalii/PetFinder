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
    private String nombrePila;
    private String fotoUrl;
    private String descripcion;
    private String localizacion;

    @ToString.Exclude
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Animal> animalesSubidos;


    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new ArrayList<>();
}