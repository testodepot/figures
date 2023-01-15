package pl.kurs.figures.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.figures.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM figures.users  WHERE login = :login) THEN 'true' ELSE 'false' END", nativeQuery = true)
    boolean existsByLogin(String login);
}
