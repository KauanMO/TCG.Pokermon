package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import repositories.UserRepository;
import rest.dtos.user.CreateUserDTO;
import rest.dtos.user.LoginDTO;
import services.exceptions.IncorrectPasswordException;
import services.exceptions.UserNotFoundException;

import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository repository;
    @Inject
    private TokenService tokenService;

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
        return getUserInfo(tokenService.getUserId());
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