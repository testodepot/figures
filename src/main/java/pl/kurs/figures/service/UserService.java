package pl.kurs.figures.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.dto.RoleDto;
import pl.kurs.figures.dto.UserDto;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepo;

    private RoleService roleService;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User add(User userToSave) {
        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        Role creatorRole = roleService.getRole("CREATOR");
        userToSave.addRole(creatorRole);
        userToSave.setCreatedFigures(new HashSet<>());
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


    public Page<UserDto> mapPageUsersToDto(Page<User> users) {
        return users.map(user -> {
            UserDto dto = new UserDto();
            Set<FigureDto> createdFiguresDto = new HashSet<>();
            Set<RoleDto> roleDtoSet = new HashSet<>();
            Set<Role> roleSet = user.getRoles();
            Set<AbstractFigure> createdFigures = user.getCreatedFigures();
            for (AbstractFigure f : createdFigures) {
                fulfillSetWithDtos(createdFiguresDto, f);
            }
            for (Role r : roleSet) {
                RoleDto roleDto = new RoleDto();
                roleDto.setId(r.getId());
                roleDto.setName(r.getName());
                roleDtoSet.add(roleDto);
            }
            dto.setRoles(roleDtoSet);
            dto.setCreatedFigures(createdFiguresDto);
            dto.setId(user.getId());
            dto.setAmountOfCreatedFigures(dto.getCreatedFigures().size());
            return dto;
        });
    }

    private void fulfillSetWithDtos(Set<FigureDto> emptyFiguresDto, AbstractFigure f) {
        FigureDto dto = new FigureDto();
        dto.setId(f.getId());
        dto.setType(f.getType());
        dto.setVersion(f.getVersion());
        dto.setCreatedBy(f.getCreatedBy().getLogin());
        dto.setCreatedAt(f.getCreatedAt());
        dto.setLastModifiedAt(f.getLastModifiedAt());
        dto.setLastModifiedBy(f.getLastModifiedBy());
        dto.setPerimeter(f.calculatePerimeterForFigure().doubleValue());
        dto.setArea(f.calculateAreaForFigure().doubleValue());
        emptyFiguresDto.add(dto);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

}
