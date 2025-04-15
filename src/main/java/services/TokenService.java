package services;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import models.User;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class TokenService {
    public String generateToken(User user, List<String> groups) {
        long duration = 3600;
        long currentTime = System.currentTimeMillis() / 1000;

        return Jwt
                .issuer("pokermon_issuer")
                .upn(user.getUsername())
                .groups(new HashSet<>(groups))
                .claim("userId", user.getId())
                .issuedAt(currentTime)
                .expiresAt(currentTime + duration)
                .sign();
    }
}