package services;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import repositories.UserRepository;
import rest.dtos.user.CreateUserDTO;
import rest.dtos.user.LoginDTO;
import services.exceptions.IncorrectPasswordException;
import services.exceptions.UserNotFoundException;
import websocket.dto.OutCardDTO;

import java.util.List;
import java.util.Optional;

@RequestScoped
public class UserService {
    @Inject
    private UserRepository repository;

    @Inject
    @Claim("userId")
    Long userId;

    @Inject
    JWTParser parser;

    @Transactional
    public User registerUser(CreateUserDTO dto) {
        User newUser = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .build();

        repository.persist(newUser);

        return newUser;
    }

    public User login(LoginDTO dto) {
        User userFound = repository.findByEmail(dto.email()).orElseThrow(UserNotFoundException::new);

        if (!userFound.getPassword().equals(dto.password())) throw new IncorrectPasswordException();

        return userFound;
    }

    public User getUserInfo(Long userId) {
        User userFound = repository.findById(userId);

        if (userId == null) throw new UserNotFoundException();

        return userFound;
    }

    public User getCurrentUserInfo() {
        return getUserInfo(this.userId);
    }

    public Optional<User> findUserById(Long userId) {
        User userFound = repository.findById(userId);

        return Optional.of(userFound);
    }

    @Transactional
    public void updateUserBalance(User user, Double newBalance) {
        user.setBalance(newBalance);

        repository.persist(user);
    }
}