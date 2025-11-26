package org.webvibecourse.be.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
/**
 * =====================================================================================
 *  JwtService
 *  -------------------------
 * Service responsible for the entire process of working with JWT in the system:

 *
 * 1. Create Access Token & Refresh Token
 * - Access Token: used to authenticate each API request
 * - Refresh Token: used to create a new access token when the access token expires
 *
 * 2. Sign the token using the HS256 algorithm with the secret key (JWT HMAC)
 *
 * 3. Decode the token and read the information in the claims:
 * - email (subject)
 * - userId
 * - role
 * - expiration
 *
 * 4. Check if the token is valid:
 * - Token is valid or expired
 * - Token has the correct user (email) or not
 * - Token is forged or not (verify signature)
 *
 * 5. Return the token expiration time (timestamp) so that FE knows and refreshes itself before expiration.
 *
 * ==> This is the central service that helps the system process JWT clearly and consistently.
 * =====================================================================================
 */
@Component
public class JwtService {

    // ======================= CONFIG VALUES =======================

    @Value("${jwt.secret}")
    private String secret;  // Secret key used to sign token (HS256)
    @Value("${jwt.access-expiration}")
    private Long accessExpiration;  // TTL of access token (ms)
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration; // TTL of refresh token (ms)

    // ======================= SIGNING KEY =======================

    /**
     * Generate key to sign and verify JWT token using HMAC SHA256
     * @return Key for JWT HMAC signing & verifying
     */
    private Key getSignKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate Access Token
     * @param email user's email
     * @param userId user's id
     * @param role user's rolev
     * @return signed JWT access token (String)
     */
    public String generateAccessToken(String email, Long userId, String role) {
        return buildToken(email, userId, role, accessExpiration);
    }

    /**
     * Generate Refresh Token
     * @param email   user's email
     * @param userId  user's id
     * @param role    user's role
     * @return signed JWT refresh token (String)
     */
    public String generateRefreshToken(String email, Long userId, String role) {
        return buildToken(email, userId, role, refreshExpiration);
    }

    /**
     * Build JWT token with shared logic
     *
     * INPUT
     * @param email          (String) user email
     * @param userId         (Long)   user id
     * @param role           (String) user role
     * @param expirationMs   (Long)   time-to-live in milliseconds
     *
     * OUTPUT
     * @return JWT token as String
     */
    private String buildToken(String email, Long userId, String role, Long expirationMs) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // ======================= TOKEN EXTRACTION =======================

    /**
     * Extract JWT claims
     * @param token JWT token
     * @return Claims object containing all payload data
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extract email (subject)
     * @param token JWT token
     * @return email as String
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract userId
     * @param token JWT token
     * @return userId as Long
     */
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    /**
     * Extract role
     * @param token JWT token
     * @return role as String
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Extract expiration timestamp
     * @param token JWT token
     * @return expiration time in milliseconds
     */
    public long extractExpiration(String token) {
        return extractClaims(token).getExpiration().getTime();
    }

    // ======================= VALIDATION =======================

    /**
     * Validate token by email and expiration
     *
     * INPUT:
     * @param token         JWT token
     * @param email         expected email
     *
     * OUTPUT:
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }


    /**
     * Check if token is expired
     * @param token JWT token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // ======================= EXPIRATION TIMESTAMP  =======================

    /**
     * Get timestamp when access token will expire
     * @return expiration time in milliseconds from now
     */
    public long getAccessExpiration() {
        return System.currentTimeMillis() + accessExpiration;
    }

    /**
     * Get timestamp when refresh token will expire
     * @return expiration time in milliseconds from now
     */
    public long getRefreshExpiration() {
        return System.currentTimeMillis() + refreshExpiration;
    }
}
