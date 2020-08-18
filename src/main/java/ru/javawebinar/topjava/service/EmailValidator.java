package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;

@Component
public class EmailValidator {

    public final UserRepository repository;

    @Autowired
    public EmailValidator(UserRepository repository) {
        this.repository = repository;
    }

    public void checkEmail(String email, int id) {
        User user;
        if ((user = repository.getByEmail(email)) != null) {
            if (user.getId() != id)
                throw new IllegalRequestDataException("User with this email already exists");
        }
    }

    public void checkEmail(String email) {
        User user;
        if ((user = repository.getByEmail(email)) != null) {
            throw new IllegalRequestDataException("User with this email already exists");
        }
    }
}
