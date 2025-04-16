package services;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import models.User;
import org.eclipse.microprofile.jwt.Claim;
import services.exceptions.UserWithoutRoleException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RequestScoped
@Getter
public class TokenService {
    @Inject
    @Claim("userId")
    private Long userId;

    public String generateToken(User user) {
        long duration = 3600;
        long currentTime = System.currentTimeMillis() / 1000;

        if (user.getRole() == null) throw new UserWithoutRoleException();

        return Jwt
                .issuer("pokermon_issuer")
                .upn(user.getUsername())
                .groups(new HashSet<>(roles.get(user.getRole())))
                .claim("userId", user.getId())
                .issuedAt(currentTime)
                .expiresAt(currentTime + duration)
                .sign();
    }

    Map<Integer, List<String>> roles = Map.of(
            1, List.of("USER"),
            2, List.of("USER", "ADMIN")
    );
}