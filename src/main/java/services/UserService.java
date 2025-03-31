package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import repositories.UserRepository;
import rest.dtos.user.CreateUserDTO;

import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository repository;

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