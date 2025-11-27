package edu.vedoque.seguridadbase.repository;

import edu.vedoque.seguridadbase.entity.Role;
import edu.vedoque.seguridadbase.entity.User;
import edu.vedoque.seguridadbase.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    public List<UserRole> findByUser(User user);

}
