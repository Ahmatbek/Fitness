package kg.biamino.projects.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil implements Serializable {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public String generateToken(String  username) {
        return buildToken(new HashMap<>(), username);
    }

    public void validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            throw new BadCredentialsException("Invalid token");
        }
    }

    public SecretKey getSecretKey() {
        byte[] secret = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(secret);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsExtractor){
        return claimsExtractor.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {
       try{
           return Jwts
                   .parserBuilder()
                   .setSigningKey(getSecretKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
       }catch (Exception e){
           throw new BadCredentialsException("Invalid token");
       }
    }

    public String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiration))
                .signWith(getSecretKey())
                .compact();
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractIdToken(String token) {
        return extractClaim(token, Claims::getId);
    }

}
