package pl.kurs.figures.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {


    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findByUserId(Integer id);

    @Query("DELETE FROM User u WHERE u.id = :id")
    void delete(Integer id);

    Optional<User> findByLogin(String login);


    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM figures.users  WHERE login = :login) THEN 'true' ELSE 'false' END", nativeQuery = true)
    boolean existsByLogin(String login);
}
