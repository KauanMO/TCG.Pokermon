package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import repositories.UserRepository;
import rest.dtos.user.CreateUserDTO;
import rest.dtos.user.LoginDTO;
import services.exceptions.EmailAlreadyTakenException;
import services.exceptions.IncorrectPasswordException;
import services.exceptions.UserNotFoundException;
import services.exceptions.UsernameAlreadyTakenException;

import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository repository;
    @Inject
    private TokenService tokenService;

    @Transactional
    public User registerUser(CreateUserDTO dto) {
        if (findByUsername(dto.username()).isPresent()) throw new UsernameAlreadyTakenException(dto.username());
        if (findByEmail(dto.email()).isPresent()) throw new EmailAlreadyTakenException(dto.email());

        User newUser = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .role(1)
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

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public void updateUserBalance(User user, Double newBalance) {
        user.setBalance(newBalance);

        repository.persist(user);
    }

    @Transactional
    public void updateUserFavoritePokemonCode(Integer pokemonCode) {
        User userFound = repository.findById(tokenService.getUserId());

        System.out.println(tokenService.getUserId());
        System.out.println(userFound);

        userFound.setFavoritePokemonCode(pokemonCode);
        repository.persist(userFound);
    }
}