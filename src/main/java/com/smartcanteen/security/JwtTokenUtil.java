
package com.smartcanteen.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String getUsernameFromJwtToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to extract all claims from token (using modern JJWT 0.12.x approach)
    private Claims extractAllClaims(String token) {
        // This uses parserBuilder() and parseSignedClaims() which are standard for JJWT 0.12.x
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token) // Changed from parseClaimsJws() to parseSignedClaims()
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Helper method to create the token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to validate token against user details and expiration
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromJwtToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Method to check if token is expired
    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromJwtToken(token).before(new Date());
    }

    // Method to get expiration date from token
    public Date getExpirationDateFromJwtToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Method to validate JWT token signature (basic validation)
    public Boolean validateJwtToken(String token) {
        try {
            // This uses parserBuilder() and parseSignedClaims() which are standard for JJWT 0.12.x
            Jwts.parser().setSigningKey(getSigningKey()).build().parseSignedClaims(token); // Changed from parseClaimsJws() to parseSignedClaims()
            return true;
        } catch (Exception e) {
            logger.error("JWT Validation Error: {}", e.getMessage());
            return false;
        }
    }

    // Helper method to get the signing key from secret
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}