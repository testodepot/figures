package pl.kurs.figures.security;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank(message = "login cant be blank or null!")
    @Length(min = 1, max = 50)
    private String login;

    @NotBlank(message = "password cant be blank or null!")
    @Length(min = 1, max = 10)
    private String password;


    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
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
}
