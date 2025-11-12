package com.sylvester.dempproject.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    public String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;

    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3600 * 1000 + 10))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        try {
            System.out.println("Attempting to verify JWT token");
            Jwts.parser().verifyWith((SecretKey) key()).build()
                    .parseSignedClaims(authToken);
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Expired JWT token: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
