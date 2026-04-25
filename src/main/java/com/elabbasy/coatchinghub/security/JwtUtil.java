package com.elabbasy.coatchinghub.security;

import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration-ms}")
    private Long accessTokenExpirationMs;

    public String generateAccessToken(User user, RoleName activeRole) {
        return generateToken(user, activeRole, accessTokenExpirationMs);
    }

    public RoleName resolvePrimaryRole(User user) {
        if (user == null || user.getRoles() == null) {
            return null;
        }

        if (user.getRoles().stream().anyMatch(role -> RoleName.ADMIN.name().equals(role.getName()))) {
            return RoleName.ADMIN;
        }
        if (user.getRoles().stream().anyMatch(role -> RoleName.COACH.name().equals(role.getName()))) {
            return RoleName.COACH;
        }
        if (user.getRoles().stream().anyMatch(role -> RoleName.COACHEE.name().equals(role.getName()))) {
            return RoleName.COACHEE;
        }

        return null;
    }

    private String generateToken(User user, RoleName activeRole, Long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", new CustomUserDetails(user).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        claims.put("id", user.getId());
        if (activeRole != null) {
            claims.put("roleName", activeRole.name());
        }
        addProfileClaims(claims, user, activeRole);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private void addProfileClaims(Map<String, Object> claims, User user, RoleName activeRole) {
        if (activeRole == null || user == null) {
            return;
        }

        switch (activeRole) {
            case ADMIN -> {
                if (user.getAdmin() != null) {
                    claims.put("adminId", user.getAdmin().getId());
                    claims.put("name", user.getAdmin().getFullName());
                }
            }
            case COACH -> {
                if (user.getCoach() != null) {
                    claims.put("coachId", user.getCoach().getId());
                    claims.put("name", user.getCoach().getFullNameEn() != null && !user.getCoach().getFullNameEn().isBlank()
                            ? user.getCoach().getFullNameEn()
                            : user.getCoach().getFullNameAr());
                }
            }
            case COACHEE -> {
                if (user.getCoachee() != null) {
                    claims.put("coacheeId", user.getCoachee().getId());
                    claims.put("name", user.getCoachee().getFullName());
                }
            }
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}

