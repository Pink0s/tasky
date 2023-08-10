package com.tasky.api.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Service class for JWT (JSON Web Token) utility functions.
 */
@Service
public class JwtUtility {

    @Value("#{'${server.secret_key}'}")
    private String SECRET_KEY;

    @Value("#{'${server.issuer}'}")
    private String ISSUER;

    /**
     * Issues a JWT token with the provided subject and default claims.
     *
     * @param subject The subject of the token.
     * @return The issued JWT token.
     */
    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    /**
     * Issues a JWT token with the provided subject and scopes.
     *
     * @param subject The subject of the token.
     * @param scopes The scopes associated with the token.
     * @return The issued JWT token.
     */
    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject,Map.of("scopes",scopes));
    }

    /**
     * Issues a JWT token with the provided subject and custom claims.
     *
     * @param subject The subject of the token.
     * @param claims Custom claims to be included in the token.
     * @return The issued JWT token.
     */
    public String issueToken(String subject, Map<String, Object> claims) {

        Date currentDate = Date.from(Instant.now());
        Date expirationDate = Date.from(Instant.now().plus(15, ChronoUnit.DAYS));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate).signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Retrieves the subject from a JWT token.
     *
     * @param token The JWT token.
     * @return The subject of the token.
     */
    public String getSubject(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    /**
     * Retrieves the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date getExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    /**
     * Retrieves the claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The claims of the token.
     */
    public Claims getClaims (String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key for JWT token generation.
     *
     * @return The signing key used for creating JWT tokens.
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Checks if a JWT token is valid for a specific user.
     *
     * @param jwt The JWT token.
     * @param username The username to be checked against the token's subject.
     * @return True if the token is valid for the user, false otherwise.
     */
    public Boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);

        return subject.equals(username) && !isTokenExpired(jwt);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param jwt The JWT token to be checked.
     * @return True if the token has expired, false if it is still valid.
     */
    private Boolean isTokenExpired(String jwt) {
        return getClaims(jwt).getExpiration().before(Date.from(Instant.now()));
    }
}
