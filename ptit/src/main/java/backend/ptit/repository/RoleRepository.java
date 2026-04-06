package backend.ptit.repository;

import backend.ptit.entity.ERole;
import backend.ptit.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role>findByName(ERole name);
}
