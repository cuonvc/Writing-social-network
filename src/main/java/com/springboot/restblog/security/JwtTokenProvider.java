package com.springboot.restblog.security;

import com.springboot.restblog.exception.APIException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    // Đoạn secret này là bí mật, chỉ có phía server biết
    @Value("${app.jwt-secret}")  //map from application.properties
    private String jwtSecret;

    //Thời gian có hiệu lực của chuỗi jwt
    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;  //milliseconds

    //genarate token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();  //chỗ này là email
        System.out.println(username);
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return token;
    }

    //get username from token
    public String getUsernameFormJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
        } catch (MalformedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }
    }
}
