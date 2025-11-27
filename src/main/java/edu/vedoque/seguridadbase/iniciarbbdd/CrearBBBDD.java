package edu.vedoque.seguridadbase.iniciarbbdd;

import edu.vedoque.seguridadbase.entity.Role;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.entity.UserRole;
import edu.vedoque.seguridadbase.repository.RoleRepository;
import edu.vedoque.seguridadbase.repository.UserRoleRepository;
import edu.vedoque.seguridadbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CrearBBBDD implements CommandLineRunner{
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.findAll().isEmpty()){
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);

            Role role2 = new Role();
            role2.setName("ROLE_USER");
            roleRepository.save(role2);

            User user=new User();
            user.setName("Javi");
            user.setEmail("galiapinerojavier@gmail.com");
            user.setPassword("1234");
            userService.saveCifrandoPassword(user);

            //Le asigno rol de ADMIN
            UserRole userRole = new UserRole();
            userRole.setRole(role); //role es el de admin
            userRole.setUser(user);
            userRoleRepository.save(userRole);



        }

    }
}
