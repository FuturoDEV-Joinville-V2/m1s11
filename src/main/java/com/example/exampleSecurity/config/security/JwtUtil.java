package com.example.exampleSecurity.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("79f770928a465502562bc38d63417aac860b7212edb1109738998dce266696063b02f90f5fecea28e6a4b6d5272a1f165c0402f51b06847517fd0e793e473bf22aaacb35702cfc6b4e379e0bbedb1dbb420d1b66769a9d0d09d28624836d7b74efbc36f3cba7e9e04dbd97a51041a12e72c6776913c0ba442831a85453a73f61843d6794f65a4ddbb8353c170f0e512e106887054f30515be923bda41d82d4c96e593488b165fa0347ec44d952de2d8d3cab65934f27689b7918a83345b2931efc8ad3722f78f29086218722624639cec31915353e14796c35ef9b9c47d1d1fc98d0f02ddc30601c1fd471287b4377b688e834b3126591b501cef6c900747d22".getBytes());

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = Map.of(
                "roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public List<String> extractRoles(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", List.class);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        try{
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }catch(Exception e){
            return false;
        }
    }

    public String extractUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean isTokenExpired(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().before(new Date());
    }
}
