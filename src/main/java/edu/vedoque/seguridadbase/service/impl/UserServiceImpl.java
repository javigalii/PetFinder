package edu.vedoque.seguridadbase.service.impl;

import edu.vedoque.seguridadbase.dto.UserDto;
import edu.vedoque.seguridadbase.entity.Role;
import edu.vedoque.seguridadbase.entity.User;
// Si usas la relación directa @ManyToMany en User, UserRole quizás ya no sea necesaria explícitamente,
// pero mantengo tu estructura de repositorio si la tienes creada.
import edu.vedoque.seguridadbase.entity.UserRole;
import edu.vedoque.seguridadbase.repository.RoleRepository;
import edu.vedoque.seguridadbase.repository.UserRepository;
import edu.vedoque.seguridadbase.repository.UserRoleRepository;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRoleRepository userRoleRepository;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();

        // CORRECCIÓN 1: Usamos setNombrePila en vez de setName
        // Unimos Nombre y Apellido del DTO para guardarlo en nombrePila
        user.setNombrePila(userDto.getFirstName() + " " + userDto.getLastName());

        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Inicializamos valores por defecto para el perfil para evitar nulos
        user.setLocalizacion("Sin especificar");
        user.setDescripcion("Nuevo usuario");

        userRepository.save(user);

        // Asignación de rol
        Role role = roleRepository.findByName("ROLE_USER");
        if(role == null){
            role = checkRoleExist();
        }
        userRoleRepository.save(new UserRole(user, role));
    }

    // Método auxiliar por si la base de datos está vacía de roles
    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void saveCifrandoPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();

        // CORRECCIÓN 2: Usamos getNombrePila en vez de getName
        String nombreCompleto = user.getNombrePila();

        if(nombreCompleto != null) {
            String[] partes = nombreCompleto.split(" ");
            userDto.setFirstName(partes[0]);

            // Evitamos error si el usuario solo puso un nombre sin apellidos
            if(partes.length > 1) {
                userDto.setLastName(partes[1]);
            } else {
                userDto.setLastName("");
            }
        }

        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    // Nota: Si usaste la entidad User que te pasé con @ManyToMany List<Role>,
    // esta función podría simplificarse usando user.getRoles(), pero si tienes UserRole intermedio, déjala así.
    public List<Role> conseguirRolesByUser(User user){
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        List<Role> roles = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            roles.add(userRole.getRole());
        }
        return roles;
    }
}