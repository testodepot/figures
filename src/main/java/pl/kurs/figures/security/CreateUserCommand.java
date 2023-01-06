package pl.kurs.figures.security;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public class CreateUserCommand {

    @NotBlank(message = "login cannot be blank or null!")
    @Length(max = 50, message = "login should not exceed 50 signs!")
    private String login;

    @NotBlank(message = "name cannot be blank or null!")
    @Pattern(regexp = "[A-Za-z]+", message = "name should contains only letters!")
    private String name;

    @NotBlank(message = "surname cannot be blank or null!")
    @Pattern(regexp = "[A-Za-z]+", message = "surname should contains only letters!")
    private String surname;

    @NotBlank(message = "password cannot be blank or null!")
    @Length(min = 3, max = 64, message = "password should be at least 3 signs and max 64 signs!")
    private String password;

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
