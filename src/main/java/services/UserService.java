package services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import models.User;
import repositories.UserRepository;
import rest.dtos.user.CreateUserDTO;

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
}