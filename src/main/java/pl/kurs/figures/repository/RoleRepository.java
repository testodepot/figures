package pl.kurs.figures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.figures.model.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

}
