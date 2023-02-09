package pl.kurs.figures.utils;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.kurs.figures.model.User;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final long EXPIRE_DURATION = 60 * 60 * 1000; // 1 hour

    private static final long REFRESH_EXPIRE_DURATION = 30 * 60 * 1000; // 0,5 hour

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getLogin()))
                .setIssuer("CodeJava")
                .claim("roles", user.getRoles().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .claim("roles", user.getRoles().toString())
                .setSubject(String.format("%s,%s", user.getId(), user.getLogin()))
                .setIssuedAt(new Date())
                .setIssuer("CodeJava")
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUserNameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String claimsSubject = claims.getSubject();
        return StringUtils.substring(claimsSubject, 2);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace", ex);
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }
        return false;
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }




}
