package pl.kurs.figures.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateUserCommand;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.dto.RoleDto;
import pl.kurs.figures.dto.UserDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.service.RoleService;
import pl.kurs.figures.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private ModelMapper mapper;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private RoleService roleService;


    public UserController(ModelMapper mapper, PasswordEncoder passwordEncoder, UserService userService, RoleService roleService) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid CreateUserCommand createUserCommand) {
        User newUser = mapper.map(createUserCommand, User.class);
        newUser.setPassword(passwordEncoder.encode(createUserCommand.getPassword()));
        Role creatorRole = roleService.getRole("CREATOR");
        userService.prepareUserToSave(newUser, creatorRole);
        User savedUser = userService.add(newUser);
        Set<RoleDto> roleDtos = userService.getRoleDtoSet(savedUser);
        Set<FigureDto> createdFiguresDto = getCreatedFiguresSetDto(savedUser);
        return ResponseEntity.ok(new UserDto(savedUser.getId(), savedUser.getLogin(), savedUser.getPassword(), roleDtos, createdFiguresDto));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@PageableDefault Pageable pageable) {
        Page<User> allUsers = userService.findAllUsers(pageable);
        Page<UserDto> userDtos = allUsers.map(x -> mapper.map(x, UserDto.class));
        userDtos.map(userDto -> {
            Set<FigureDto> createdFiguresDto = new HashSet<>();
            Set<AbstractFigure> createdFigures = userService.findByLogin(userDto.getLogin()).getCreatedFigures();
            for (AbstractFigure f : createdFigures) {
                FigureDto dto = mapper.map(f, FigureDto.class);
                dto.setPerimeter(f.calculatePerimeterForFigureDto(f).doubleValue());
                dto.setArea(f.calculateAreaForFigureDto(f).doubleValue());
                createdFiguresDto.add(dto);
            }
            userDto.setAmountOfCreatedFigures(createdFiguresDto.size());
            return createdFiguresDto;
        });
        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }


    private Set<FigureDto> getCreatedFiguresSetDto(User savedUser) {
        Set<AbstractFigure> createdFigures = savedUser.getCreatedFigures();
        Set<FigureDto> createdFiguresDto = new HashSet<>();

        for (AbstractFigure f : createdFigures) {
            FigureDto dto = mapper.map(f, FigureDto.class);
            dto.setPerimeter(f.calculatePerimeterForFigureDto(f).doubleValue());
            dto.setArea(f.calculateAreaForFigureDto(f).doubleValue());
            createdFiguresDto.add(dto);
        }

        return createdFiguresDto;
    }


}
