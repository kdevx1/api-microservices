package com.devx.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;
import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import java.util.List;

@Service
public class JwtService {

        private final String SECRET_KEY = "devx-super-secret-key-very-secure-123456";
       

        private Key getSignKey() {
               return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        }

        // 🔓 extrai username (email)
        public String extractUsername(String token) {
                return extractClaim(token, Claims::getSubject);
        }

        // 🔓 extrai qualquer claim
        public <T> T extractClaim(String token, Function<Claims, T> resolver) {
                final Claims claims = extractAllClaims(token);
                return resolver.apply(claims);
        }

        // 🔓 gera token
        public String generateToken(User user) {
               
            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("name", user.getName())
                    .claim("role", user.getRole())
                    .claim("permissions", user.getRole().getPermissions())
                    .claim("avatar", user.getAvatar())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        private List<String> getPermissionsByRole(Role role) {
            if (role == Role.ROLE_ADMIN) {
                return List.of("ADMIN_PANEL", "DASHBOARD_VIEW");
            }
            return List.of("DASHBOARD_VIEW");
        }

        // 🔐 valida token
        public boolean isTokenValid(String token) {     
           try {
                   extractAllClaims(token);
                   return true;
            } catch (Exception e) {
                   return false;
                }
        }
        // // ⏳ verifica expiração
        // private boolean isTokenExpired(String token) {
        //         return extractExpiration(token).before(new Date());
        // }

        // private Date extractExpiration(String token) {
        //         return extractClaim(token, Claims::getExpiration);
        // }

        // 🔍 parse do token
        private Claims extractAllClaims(String token) {
                return Jwts
                        .parserBuilder()
                        .setSigningKey(getSignKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        }
}