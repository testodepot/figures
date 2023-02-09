package pl.kurs.figures.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.kurs.figures.exception.AppException;

@Component
public class RequestHandler {


    public String getJwtFromStringRequest(String request) {
        if (StringUtils.hasText(request) && request.startsWith("Bearer ")) {
            return request.substring(7, request.length());
        }
        throw new AppException("Jwt is empty or Bearer missing", HttpStatus.UNAUTHORIZED.toString());
    }


}
