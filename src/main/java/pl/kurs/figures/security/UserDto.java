package pl.kurs.figures.security;

import pl.kurs.figures.dto.FigureDto;

import java.util.Set;

public class UserDto {

    private Integer id;
    private String login;
    private String password;
    private Set<RoleDto> roles;
    private Set<FigureDto> createdFigures;

    public UserDto() {
    }

    public UserDto(Integer id, String login, String password, Set<RoleDto> roles, Set<FigureDto> createdFigures) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.roles = roles;
        this.createdFigures = createdFigures;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    public Set<FigureDto> getCreatedFigures() {
        return createdFigures;
    }

    public void setCreatedFigures(Set<FigureDto> createdFigures) {
        this.createdFigures = createdFigures;
    }
}
