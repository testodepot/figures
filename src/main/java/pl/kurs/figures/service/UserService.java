package pl.kurs.figures.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kurs.figures.dto.RoleDto;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
                () -> new UsernameNotFoundException("No user of this login available: " + login + "!"));
    }

    public boolean existsByLogin(String login) {
        return userRepo.existsByLogin(login);
    }


    public Set<RoleDto> getRoleDtoSet(User savedUser) {
        return savedUser.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
    }


    public void prepareUserToSave(User userToSave, Role role) {
        userToSave.addRole(role);
        userToSave.setCreatedFigures(new HashSet<>());
    }

    public Page<User> findAllUsers(Pageable pageable) {return userRepo.findAll(pageable);}

}
