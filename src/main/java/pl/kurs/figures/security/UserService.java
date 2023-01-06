package pl.kurs.figures.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public User add(User userToSave) {
        return userRepo.save(Optional.ofNullable(userToSave)
                .filter(x -> Objects.isNull(x.getId()))
                .orElseThrow(() -> new BadEntityException("User")));
    }

    public User edit(User user) {
        return userRepo.save(Optional.ofNullable(user)
                .filter(x -> Objects.nonNull(x.getId()))
                .orElseThrow(() -> new BadEntityException("User")));
    }

    public User findByLogin(String login) {
        return userRepo.findByLogin(login).orElseThrow(
                () -> new UsernameNotFoundException(login));
    }


    public void checkUserRoles(User user, Role role) {
        boolean contains = user.getRoles().contains(role);

        if (!contains) {
            throw new EntityNotFoundException(Long.valueOf(role.getId()), "Role");
        }
    }

    public boolean existsByLogin(String login) {
        return userRepo.existsByLogin(login);
    }

    public Page<User> findAllUsers(Pageable pageable) {return userRepo.findAll(pageable);}

    public List<User> findUsers() {
        return userRepo.findAll();
    }


    public void delete(Integer id) {
        User user = userRepo.findByUserId(id);

        if (user == null) {
            throw new EntityNotFoundException(Long.valueOf(id), "User");
        }

        user.deleteAllRoles();
        userRepo.save(user);
        userRepo.deleteById(id);
    }
}
