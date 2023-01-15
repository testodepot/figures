package pl.kurs.figures.security;

public class AuthResponse {

    private String login;

    private String accessToken;

    private String refreshToken;


    public AuthResponse(String login, String accessToken, String refreshToken) {
        this.login = login;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
