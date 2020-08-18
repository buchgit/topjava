package ru.javawebinar.topjava.web.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.EmailValidator;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;

import javax.validation.Valid;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@RestController
@RequestMapping(ProfileRestController.REST_URL)
public class ProfileRestController extends AbstractUserController {
    static final String REST_URL = "/rest/profile";

    private final EmailValidator emailValidator;

    public ProfileRestController(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User get() {
        return super.get(authUserId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        super.delete(authUserId());
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserTo userTo, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalRequestDataException("invalid request data");
        } else {
            emailValidator.checkEmail(userTo.getEmail());
            super.create(userTo);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@Valid @RequestBody UserTo userTo, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalRequestDataException("invalid request data");
        } else {
            emailValidator.checkEmail(userTo.getEmail(), userTo.getId());
            super.update(userTo, authUserId());
        }
    }

    @GetMapping(value = "/text")
    public String testUTF() {
        return "Русский текст";
    }

    @GetMapping("/with-meals")
    public User getWithMeals() {
        return super.getWithMeals(authUserId());
    }
}