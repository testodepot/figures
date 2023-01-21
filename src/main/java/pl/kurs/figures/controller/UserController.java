package pl.kurs.figures.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.command.CreateUserCommand;
import pl.kurs.figures.dto.UserDto;
import pl.kurs.figures.model.User;
import pl.kurs.figures.service.UserService;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private ModelMapper mapper;

    private UserService userService;

    public UserController(ModelMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid CreateUserCommand createUserCommand) {
        User newUser = mapper.map(createUserCommand, User.class);
        userService.add(newUser);
        return ResponseEntity.ok(mapper.map(newUser, UserDto.class));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@PageableDefault Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.mapPageUsersToDto(userService.findAllUsers(pageable)));
    }


}
