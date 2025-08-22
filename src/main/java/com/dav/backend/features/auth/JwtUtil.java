package com.dav.backend.features.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // Prefer >= 64 chars for HS256. Replace with a secret manager.
    private final String secret = "s3cure!K3y_2025#JwtAuthSecret$Val!dation@12345_some_extra_padding_to_make_it_64+";
    private final long jwtExpirationMs = 86400000L; // 24h
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String username, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.replace("ROLE_", "")) // store "STUDENT" / "EMPLOYEE"
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return "ROLE_" + getAllClaims(token).get("role", String.class);
    }


    public boolean validateToken(String token) {
        try {
            getAllClaims(token); // will throw if invalid/expired
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Overload used by your filter
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getAllClaims(token);
            String subject = claims.getSubject();
            Date exp = claims.getExpiration();
            return subject != null
                    && subject.equals(userDetails.getUsername())
                    && exp != null
                    && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


