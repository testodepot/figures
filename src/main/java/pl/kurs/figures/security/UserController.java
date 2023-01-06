package pl.kurs.figures.security;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.dto.FigureDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.service.AbstractFigureViewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private ModelMapper mapper;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private RoleService roleService;

    private AbstractFigureViewService abstractFigureViewService;

    public UserController(ModelMapper mapper, PasswordEncoder passwordEncoder, UserService userService, RoleService roleService, AbstractFigureViewService abstractFigureViewService) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.abstractFigureViewService = abstractFigureViewService;
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid CreateUserCommand createUserCommand) {
        User newUser = mapper.map(createUserCommand, User.class);
        newUser.setPassword(passwordEncoder.encode(createUserCommand.getPassword()));
        newUser.addRole(roleService.getRole("CREATOR"));
        newUser.setCreatedFigures(new HashSet<>());
        User savedUser = userService.add(newUser);
        Set<RoleDto> roleDtos = getRoleDtoSet(savedUser);
        Set<FigureDto> createdFiguresDto = getCreatedFiguresSetDto(savedUser);
        return ResponseEntity.ok(new UserDto(savedUser.getId(), savedUser.getLogin(), savedUser.getPassword(), roleDtos, createdFiguresDto));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@PageableDefault Pageable pageable) {
        Page<User> allUsers = userService.findAllUsers(pageable);
        Page<UserDto> userDtos = allUsers.map(x -> mapper.map(x, UserDto.class));
        List<UserDto> userDtosList = userDtos.stream().collect(Collectors.toList());
        setAreaAndPerimeterForFigures(userDtosList);
        PageImpl<UserDto> userDtoPage = new PageImpl<>(userDtosList, pageable, pageable.getOffset());
        return ResponseEntity.status(HttpStatus.OK).body(userDtoPage);
    }

    private void setAreaAndPerimeterForFigures(List<UserDto> userDtosList) {
        for (UserDto u : userDtosList) {
            Set<FigureDto> createdFigures = u.getCreatedFigures();
            for (FigureDto f : createdFigures) {
                f.setArea(abstractFigureViewService.getById(f.getId()).getArea().doubleValue());
                f.setPerimeter(abstractFigureViewService.getById(f.getId()).getPerimeter().doubleValue());
            }
        }
    }


    @NotNull
    private Set<RoleDto> getRoleDtoSet(User savedUser) {
        return savedUser.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
    }

    private Set<FigureDto> getCreatedFiguresSetDto(User savedUser) {
        Set<AbstractFigure> createdFigures = savedUser.getCreatedFigures();
        Set<FigureDto> createdFiguresDto = new HashSet<>();

        for (AbstractFigure f : createdFigures) {
            FigureDto dto = mapper.map(f, FigureDto.class);
            createdFiguresDto.add(dto);
        }

        for (FigureDto f : createdFiguresDto) {
            f.setArea(abstractFigureViewService.getById(f.getId()).getArea().doubleValue());
            f.setPerimeter(abstractFigureViewService.getById(f.getId()).getPerimeter().doubleValue());
        }

        return createdFiguresDto;
    }


}
